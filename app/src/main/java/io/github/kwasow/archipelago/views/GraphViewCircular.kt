package io.github.kwasow.archipelago.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import io.github.kwasow.archipelago.utils.MaterialColors
import org.javamoney.moneta.Money

class GraphViewCircular : View {
    private val paintCash = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintAccount = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintInvestment = Paint(Paint.ANTI_ALIAS_FLAG)
    private val centerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var cashAngle = 0F
    private var accountAngle = 0F
    private var investmentAngle = 0F
    private val oval = RectF()

    private var currencyCode = ""
    // TODO: Cannot be hardcoded
    private var sum = Money.of(0, "PLN")
    private val sumPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val percents = mutableListOf<Double>()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr) {
            init()
        }

    private fun init() {
        paintCash.color = MaterialColors.LIGHT_GREEN
        paintAccount.color = MaterialColors.YELLOW
        paintInvestment.color = MaterialColors.BLACK54

        centerCirclePaint.color = MaterialColors.BACKGROUND_WHITE
        sumPaint.color = MaterialColors.BLACK
        sumPaint.textAlign = Paint.Align.CENTER
        sumPaint.typeface = Typeface.DEFAULT_BOLD
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas == null) return

        oval.set(
            (0.5 * width - 0.45 * height).toFloat(),
            (0.05 * height).toFloat(),
            (0.5 * width + 0.45 * height).toFloat(),
            (0.95 * height).toFloat()
        )

        // Draw parts of the circles
        canvas.drawArc(
            oval,
            0F, cashAngle,
            true, paintCash
        )
        canvas.drawArc(
            oval,
            cashAngle, accountAngle,
            true, paintAccount
        )
        canvas.drawArc(
            oval,
            cashAngle + accountAngle, investmentAngle,
            true, paintInvestment
        )

        // Draw circle to make it a ring
        canvas.drawCircle(
            (width * 0.5).toFloat(),
            (height * 0.5).toFloat(),
            (height * 0.3).toFloat(),
            centerCirclePaint
        )

        // Write what the sum is
        dynamicTextSize(sumPaint, (0.5 * height).toFloat(), sum.toString())
        val cx = (width * 0.5).toFloat()
        val cy = (height) / 2 - (sumPaint.descent() + sumPaint.ascent()) / 2
        canvas.drawText(
            sum.toString(),
            cx, cy,
            sumPaint
        )
    }

    fun setData(data: List<Money>, currencyCode: String = "") {
        percents.clear()
        this.currencyCode = currencyCode

        sum = Money.of(0, currencyCode)
        data.forEach {
            sum = sum.add(it)
        }

        var percentsSum = 0.0
        for (i in data.indices) {
            if (i != data.lastIndex && !sum.isZero) {
                val percent = (data[i].number.toDouble() * 100) / sum.number.toDouble()
                percents.add(percent)
                percentsSum += percent
            } else {
                // We want everything to add up to 100%
                percents.add(100.0 - percentsSum)
            }
        }

        cashAngle = (360.0 * (percents[0] / 100.0)).toFloat()
        accountAngle = (360.0 * (percents[1] / 100.0)).toFloat()
        investmentAngle = (360.0 - cashAngle - accountAngle).toFloat()

        // Redraw the view
        postInvalidate()
    }

    private fun dynamicTextSize(paint: Paint, desiredWidth: Float, text: String) {
        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        val testTextSize = 48f

        // Get the bounds of the text, using our testTextSize.
        paint.textSize = testTextSize
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)

        // Calculate the desired size as a proportion of our testTextSize.
        val desiredTextSize: Float = testTextSize * desiredWidth / bounds.width()

        // Set the paint for that size.
        paint.textSize = desiredTextSize
    }
}
