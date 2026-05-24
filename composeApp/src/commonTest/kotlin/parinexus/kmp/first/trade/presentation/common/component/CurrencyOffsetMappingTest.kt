package parinexus.kmp.first.trade.presentation.common.component

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class CurrencyOffsetMappingTest {

    @Test
    fun originalToTransformed_mapsDigitOffsets() {
        val mapping = CurrencyOffsetMapping(
            originalText = "1234",
            formattedText = "$1,234",
        )

        assertThat(mapping.originalToTransformed(0)).isEqualTo(1)
        assertThat(mapping.originalToTransformed(3)).isEqualTo(5)
    }

    @Test
    fun transformedToOriginal_mapsFormattedOffsetsBack() {
        val mapping = CurrencyOffsetMapping(
            originalText = "1234",
            formattedText = "$1,234",
        )

        assertThat(mapping.transformedToOriginal(1)).isEqualTo(0)
        assertThat(mapping.transformedToOriginal(5)).isEqualTo(3)
    }
}
