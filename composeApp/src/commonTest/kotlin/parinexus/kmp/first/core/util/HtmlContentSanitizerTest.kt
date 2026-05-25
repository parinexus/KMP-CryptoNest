package parinexus.kmp.first.core.util

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.doesNotContain
import assertk.assertions.isEqualTo
import kotlin.test.Test

class HtmlContentSanitizerTest {

    @Test
    fun toPlainText_stripsAnchorTagsAndKeepsLinkText() {
        val html = """
            <a href="https://coinranking.com/coin/MoTuySvg7+dai-dai/">Dai (DAI)</a> has been rebranded to
            <a href="https://coinranking.com/coin/iCJJM4B36+usds-usds/">USDS (USDS).</a>
            For more information, please visit this
            <a href="https://x.com/SkyEcosystem/status/1828405625828843710/">post.</a>
        """.trimIndent()

        val plain = HtmlContentSanitizer.toPlainText(html)

        assertThat(plain).doesNotContain("<")
        assertThat(plain).doesNotContain("href")
        assertThat(plain).contains("Dai (DAI)")
        assertThat(plain).contains("USDS (USDS)")
        assertThat(plain).contains("post.")
    }

    @Test
    fun toPlainText_returnsPlainTextUnchanged() {
        assertThat(HtmlContentSanitizer.toPlainText("Hello world")).isEqualTo("Hello world")
    }
}
