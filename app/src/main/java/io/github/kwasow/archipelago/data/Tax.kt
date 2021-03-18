package io.github.kwasow.archipelago.data

data class Tax(
    val taxed: Taxed,
    val automatic: Boolean,
    val bracketStart: Int,
    val bracketEnd: Int,
    val rate: Int
) {
    companion object {
        enum class Taxed {
            INCOME, SAVINGS
        }
    }

    override fun toString(): String {
        return "Tax{$taxed, $automatic, $bracketStart, $bracketEnd, $rate%}"
    }
}
