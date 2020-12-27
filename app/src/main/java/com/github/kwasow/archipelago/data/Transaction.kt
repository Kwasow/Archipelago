package com.github.kwasow.archipelago.data

import java.io.Serializable
import java.util.*

data class Transaction(
        var date: Date,
        var name: String,
        var amount: Double,
        var details: String
) : Serializable