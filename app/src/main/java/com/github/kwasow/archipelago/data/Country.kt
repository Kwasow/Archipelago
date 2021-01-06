package com.github.kwasow.archipelago.data

data class Country(
    val name: String,
    val code: String,
    val currency: String,
    val taxAccount: Array<Tax>,
    val taxInvestment: Array<Tax>,
    val taxStock: Array<Tax>
) {
    override fun toString(): String {
        return "Country{$name, $code, $currency, $taxAccount, $taxInvestment, $taxStock}"
    }

    // Generated automatically
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Country

        if (name != other.name) return false
        if (code != other.code) return false
        if (!taxAccount.contentEquals(other.taxAccount)) return false
        if (!taxInvestment.contentEquals(other.taxInvestment)) return false
        if (!taxStock.contentEquals(other.taxStock)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + code.hashCode()
        result = 31 * result + taxAccount.contentHashCode()
        result = 31 * result + taxInvestment.contentHashCode()
        result = 31 * result + taxStock.contentHashCode()
        return result
    }
}
