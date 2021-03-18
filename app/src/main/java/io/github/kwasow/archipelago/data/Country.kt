package io.github.kwasow.archipelago.data

data class Country(
    val name: String,
    val code: String,
    val currencyCode: String,
    val taxAccount: List<Tax>,
    val taxInvestment: List<Tax>,
    val taxStock: List<Tax>,
    val taxCrypto: List<Tax>
)
