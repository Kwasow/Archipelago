package com.github.kwasow.archipelago.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.github.kwasow.archipelago.R
import com.github.kwasow.archipelago.data.Transaction
import com.github.kwasow.archipelago.utils.ArchipelagoError
import com.github.kwasow.archipelago.utils.MaterialColors

class GraphView : View {
    // Attributes
    private var colorPositive = MaterialColors.LIGHT_GREEN
    private var colorNegative = MaterialColors.RED
    var data = emptyArray<Array<Double>>()

    // Class objects
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var graphLines = floatArrayOf()

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr) {
            init(attrs)
        }

    private fun init(attrs: AttributeSet?) {
        // Read values if any present
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.GraphView)

            colorPositive =
                ta.getColor(R.styleable.GraphView_color_positive, MaterialColors.LIGHT_GREEN)
            colorNegative =
                ta.getColor(R.styleable.GraphView_color_negative, MaterialColors.RED)

            // Clean up
            ta.recycle()
        }

        // Calculate line width (1dp)
        val axisWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            1F,
            resources.displayMetrics
        )
        axisPaint.color = MaterialColors.BLACK
        axisPaint.strokeWidth = axisWidth

        val lineWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            2F,
            resources.displayMetrics
        )
        linePaint.strokeWidth = lineWidth
    }

    override fun onDraw(canvas: Canvas?) {
        // Draw background before everything else
        super.onDraw(canvas)

        if (canvas == null) return

        // Draw axis
        val axisList = floatArrayOf(
            // Start of x axis
            0F, (height * 0.95).toFloat(),
            // End of x axis
            width.toFloat(), (height * 0.95).toFloat(),
            // Start of y axis
            (width * 0.05).toFloat(), height.toFloat(),
            // End of y axis
            (width * 0.05).toFloat(), 0F
        )
        canvas.drawLines(axisList, axisPaint)
        // Draw graph
        // TODO: Calculation should be moved somewhere else
        calculateLine(width, height)
        canvas.drawLines(graphLines, linePaint)
    }

    private fun calculateLine(width: Int, height: Int) {
        // Check if array is prepared correctly
        data.forEach {
            if (it.size != 2) {
                ArchipelagoError.e("Incorrect data array provided for GraphView")
                return
            }
        }

        // Don't continue if there is nothing to take care of
        if (data.isEmpty()) return

        // We need 4 elements for every line (there are n-1 lines -> unless it's one point)
        graphLines = if (data.size == 1) {
            FloatArray(4)
        } else {
            FloatArray((data.size - 1) * 4)
        }
        val x00 = (width * 0.05).toFloat()
        // We want a 5% padding on the bottom
        val y00 = (height * 0.80).toFloat()
        // Find minimum and maximum value
        var maxY = Double.MIN_VALUE
        var minY = Double.MAX_VALUE
        var maxX = Double.MIN_VALUE
        var minX = Double.MAX_VALUE
        data.forEach {
            // Look at X
            if (it[0] < minX) {
                minX = it[0]
            }
            if (it[0] > maxX) {
                maxX = it[0]
            }

            // Look at Y
            if (it[1] < minY) {
                minY = it[1]
            }
            if (it[1] > maxY) {
                maxY = it[1]
            }
        }

        maxX -= minX
        maxY -= minY
        // Now make the minimum value 0 and scale to fit in pixel range
        // Scale:
        // on the X axis:
        //  - we want the maximum to become 0.95*width (because we have empty space at the beginning
        // on the Y axis:
        //  - we want the maximum to become 0.75*height
        data.forEach {
            it[0] = (it[0] - minX)
            if (maxX != 0.0) {
                it[0] *= (0.95 * width / maxX)
            }
            it[1] = (it[1] - minY)
            if (maxY != 0.0) {
                it[1] *= (0.75 * height / maxY)
            }
        }

        // Make sure it's sorted correctly
        data.sortBy {
            it[0]
        }

        // Now when everything is set up, let's put it into our graphLines array
        for (i in 0..(data.size - 2)) {
            // x start
            graphLines[i * 4] = x00 + data[i][0].toFloat()
            // y start
            graphLines[i * 4 + 1] = y00 - data[i][1].toFloat()
            // x end
            graphLines[i * 4 + 2] = x00 + data[i + 1][0].toFloat()
            // y end
            graphLines[i * 4 + 3] = y00 - data[i + 1][1].toFloat()
        }

        // Set up line color
        if (data[0][1] <= data[data.lastIndex][1]) {
            linePaint.color = colorPositive
        } else {
            linePaint.color = colorNegative
        }

        // Draw data after data is set and line is calculated
        // postInvalidate()
    }

    companion object {
        fun graphArrayFromTransactions(transactions: List<Transaction>): Array<Array<Double>> {
            val returnList = mutableListOf<Array<Double>>()

            // Make sure transactions are sorted by date
            val correctTransactions = transactions.toTypedArray()
            correctTransactions.sortBy {
                it.date.time
            }

            for (i in transactions.indices) {
                // Space data evenly
                val date = (i + 1).toDouble()
                val amount = if (i == 0) {
                    transactions[0].amount.toDouble()
                } else {
                    returnList[i - 1][1] + transactions[i].amount.toDouble()
                }

                returnList.add(
                    arrayOf(date, amount)
                )

                // So there are at least two points
                if (i == transactions.size - 1 && returnList.size == 1) {
                    returnList.add(
                        arrayOf(date + 1, amount)
                    )
                }
            }

            return returnList.toTypedArray()
        }
    }
}
