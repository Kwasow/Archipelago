package com.github.kwasow.archipelago.data

data class Country(
    val name: String,
    val code: String,
    val currency: String,
    val taxAccount: List<Tax>,
    val taxInvestment: List<Tax>,
    val taxStock: List<Tax>
)
