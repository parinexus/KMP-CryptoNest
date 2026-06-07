package parinexus.kmp.first.core.util

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import org.junit.Test

class FormatterAndroidTest {

    @Test
    fun formatFiat_largeAmount_includesThousandsSeparator() {
        val formatted = formatFiat(12_345.67)

        assertThat(formatted).contains("12,345")
        assertThat(formatted.startsWith("$")).isEqualTo(true)
    }

    @Test
    fun formatFiat_withoutDecimals_omitsFraction() {
        val formatted = formatFiat(amount = 5000.0, showDecimal = false)

        assertThat(formatted).isEqualTo("$5,000")
    }

    @Test
    fun formatCoinUnit_appendsSymbol() {
        val formatted = formatCoinUnit(0.12345678, "BTC")

        assertThat(formatted).contains("BTC")
    }

    @Test
    fun formatPercentage_positiveValue_hasPlusSign() {
        val formatted = formatPercentage(2.5)

        assertThat(formatted).isEqualTo("+2.50%")
    }

    @Test
    fun formatPercentage_negativeValue_hasMinusSign() {
        val formatted = formatPercentage(-1.2)

        assertThat(formatted).isEqualTo("-1.20%")
    }
}
