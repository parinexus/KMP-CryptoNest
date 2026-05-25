package parinexus.kmp.first.core.util

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import kotlin.test.Test

class HtmlNoticeParserTest {

    @Test
    fun parse_extractsClickableLinksFromAnchors() {
        val html = """
            <a href="https://coinranking.com/coin/MoTuySvg7+dai-dai/">Dai (DAI)</a> has been rebranded to
            <a href="https://coinranking.com/coin/iCJJM4B36+usds-usds/">USDS (USDS).</a>
            For more information, please visit this
            <a href="https://x.com/SkyEcosystem/status/1828405625828843710/">post.</a>
        """.trimIndent()

        val segments = HtmlNoticeParser.parse(html)

        assertThat(segments.filterIsInstance<NoticeRichTextSegment.Link>()).hasSize(3)
        val firstLink = segments[0] as NoticeRichTextSegment.Link
        assertThat(firstLink.label).isEqualTo("Dai (DAI)")
        assertThat(firstLink.url).isEqualTo("https://coinranking.com/coin/MoTuySvg7+dai-dai/")
        assertThat(segments.any { it is NoticeRichTextSegment.Text }).isEqualTo(true)
    }

    @Test
    fun parse_splitsBareUrlsInPlainText() {
        val segments = HtmlNoticeParser.parse("Visit https://sky.money/ for details.")

        assertThat(segments).hasSize(3)
        assertThat(segments[0]).isInstanceOf(NoticeRichTextSegment.Text::class)
        assertThat(segments[1]).isInstanceOf(NoticeRichTextSegment.Link::class)
        assertThat((segments[1] as NoticeRichTextSegment.Link).url).isEqualTo("https://sky.money/")
    }
}
