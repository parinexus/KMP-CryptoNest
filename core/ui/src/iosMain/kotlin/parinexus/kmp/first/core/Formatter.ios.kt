package parinexus.kmp.first.core.util

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle
import kotlin.math.abs

private val fiatNoDecimal: NSNumberFormatter = NSNumberFormatter().apply {
    numberStyle = NSNumberFormatterDecimalStyle
    minimumFractionDigits = 0u
    maximumFractionDigits = 0u
}

private val fiatTwoDecimals: NSNumberFormatter = NSNumberFormatter().apply {
    numberStyle = NSNumberFormatterDecimalStyle
    minimumFractionDigits = 2u
    maximumFractionDigits = 2u
}

private val fiatEightDecimals: NSNumberFormatter = NSNumberFormatter().apply {
    numberStyle = NSNumberFormatterDecimalStyle
    minimumFractionDigits = 8u
    maximumFractionDigits = 8u
}

private val coinUnitFormatter: NSNumberFormatter = NSNumberFormatter().apply {
    numberStyle = NSNumberFormatterDecimalStyle
    minimumFractionDigits = 8u
    maximumFractionDigits = 8u
}

private val percentFormatter: NSNumberFormatter = NSNumberFormatter().apply {
    numberStyle = NSNumberFormatterDecimalStyle
    minimumFractionDigits = 2u
    maximumFractionDigits = 2u
}

actual fun formatFiat(amount: Double, showDecimal: Boolean): String {
    val formatter = when {
        !showDecimal -> fiatNoDecimal
        amount >= 0.01 -> fiatTwoDecimals
        else -> fiatEightDecimals
    }
    return "$${formatter.stringFromNumber(NSNumber(amount)) ?: ""}"
}

actual fun formatCoinUnit(amount: Double, symbol: String): String {
    val formatted = coinUnitFormatter.stringFromNumber(NSNumber(amount)) ?: return ""
    return "$formatted $symbol"
}

actual fun formatPercentage(amount: Double): String {
    val formatted = percentFormatter.stringFromNumber(NSNumber(abs(amount))) ?: return ""
    val sign = if (amount >= 0) "+" else "-"
    return "$sign$formatted%"
}
