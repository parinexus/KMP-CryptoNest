package parinexus.kmp.first.core.util

import kotlin.math.abs

/**
 * Formats large fiat-style numbers for market cap, volume, and supply (e.g. $159.4B).
 */
fun formatCompactFiat(amount: Double): String {
    if (amount.isNaN() || amount == 0.0) return formatFiat(0.0)
    val sign = if (amount < 0) "-" else ""
    val value = abs(amount)
    val (scaled, suffix) = when {
        value >= 1_000_000_000_000 -> value / 1_000_000_000_000 to "T"
        value >= 1_000_000_000 -> value / 1_000_000_000 to "B"
        value >= 1_000_000 -> value / 1_000_000 to "M"
        value >= 1_000 -> value / 1_000 to "K"
        else -> return formatFiat(amount)
    }
    val rounded = (scaled * 10).toInt() / 10.0
    val text = if (rounded == rounded.toLong().toDouble()) {
        rounded.toLong().toString()
    } else {
        rounded.toString()
    }
    return "$sign$$text$suffix"
}
