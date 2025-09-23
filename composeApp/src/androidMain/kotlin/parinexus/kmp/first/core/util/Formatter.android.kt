package parinexus.kmp.first.core.util

import java.text.DecimalFormat
import kotlin.math.abs

private val fiatNoDecimal = DecimalFormat("#,###")
private val fiatWithDecimal = DecimalFormat("#,###.00")
private val fiatSmall = DecimalFormat("0.00")
private val fiatTiny = DecimalFormat("0.00000000")

private val coinUnitFormat = DecimalFormat("0.00000000")
private val percentFormat = DecimalFormat("0.00")

actual fun formatFiat(amount: Double, showDecimal: Boolean): String {
    val formatter = when {
        !showDecimal -> fiatNoDecimal
        amount >= 1   -> fiatWithDecimal
        amount >= 0.01 -> fiatSmall
        else -> fiatTiny
    }
    return "$${formatter.format(amount)}"
}

actual fun formatCoinUnit(amount: Double, symbol: String): String {
    return "${coinUnitFormat.format(amount)} $symbol"
}

actual fun formatPercentage(amount: Double): String {
    val sign = if (amount >= 0) "+" else "-"
    return "$sign${percentFormat.format(abs(amount))}%"
}
