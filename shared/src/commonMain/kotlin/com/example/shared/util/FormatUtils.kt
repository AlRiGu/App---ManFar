package com.example.shared.util

import kotlin.math.round

fun Double.formatPrice(): String {
    val rounded = round(this * 100) / 100.0
    val str = rounded.toString()
    val parts = str.split(".")
    val intPart = parts[0]
    val decPart = if (parts.size > 1) parts[1].padEnd(2, '0').take(2) else "00"
    return "$intPart.$decPart"
}

fun Double.formatDecimals(decimals: Int = 2): String {
    val factor = when (decimals) {
        0 -> 1.0
        1 -> 10.0
        2 -> 100.0
        else -> 100.0
    }
    val rounded = round(this * factor) / factor
    val str = rounded.toString()
    val parts = str.split(".")
    val intPart = parts[0]
    val decPart = if (parts.size > 1) parts[1].padEnd(decimals, '0').take(decimals) else "0".repeat(decimals)
    return if (decimals > 0) "$intPart.$decPart" else intPart
}
