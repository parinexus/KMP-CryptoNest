package parinexus.kmp.first.core.util

sealed interface NoticeRichTextSegment {
    data class Text(val value: String) : NoticeRichTextSegment
    data class Link(val label: String, val url: String) : NoticeRichTextSegment
}

/**
 * Parses Coinranking HTML notices into plain text and clickable link segments.
 */
object HtmlNoticeParser {

    private val anchorTag = Regex(
        """<a\s+[^>]*href\s*=\s*["']([^"']+)["'][^>]*>([\s\S]*?)</a>""",
        RegexOption.IGNORE_CASE,
    )
    private val bareUrl = Regex("""https?://[^\s<>"']+""")

    fun parse(html: String): List<NoticeRichTextSegment> {
        if (html.isBlank()) return emptyList()
        if (!html.contains('<')) return splitBareUrls(html.trim())

        val segments = mutableListOf<NoticeRichTextSegment>()
        var cursor = 0

        anchorTag.findAll(html).forEach { match ->
            if (match.range.first > cursor) {
                addPlainText(html.substring(cursor, match.range.first), segments)
            }
            val url = match.groupValues[1].trim()
            val label = stripInnerTags(match.groupValues[2])
                .let { HtmlContentSanitizer.decodeEntitiesPublic(it) }
                .trim()
            if (url.isNotEmpty() && label.isNotEmpty()) {
                segments.add(NoticeRichTextSegment.Link(label = label, url = url))
            }
            cursor = match.range.last + 1
        }

        if (cursor < html.length) {
            addPlainText(html.substring(cursor), segments)
        }

        return mergeAdjacentText(segments.flatMap { splitBareUrlsInSegment(it) })
    }

    private fun addPlainText(fragment: String, segments: MutableList<NoticeRichTextSegment>) {
        val plain = HtmlContentSanitizer.toPlainText(fragment)
        if (plain.isNotBlank()) {
            segments.add(NoticeRichTextSegment.Text(plain))
        }
    }

    private fun splitBareUrlsInSegment(segment: NoticeRichTextSegment): List<NoticeRichTextSegment> =
        when (segment) {
            is NoticeRichTextSegment.Link -> listOf(segment)
            is NoticeRichTextSegment.Text -> splitBareUrls(segment.value)
        }

    private fun splitBareUrls(text: String): List<NoticeRichTextSegment> {
        if (text.isBlank()) return emptyList()
        val result = mutableListOf<NoticeRichTextSegment>()
        var cursor = 0
        bareUrl.findAll(text).forEach { match ->
            if (match.range.first > cursor) {
                val before = text.substring(cursor, match.range.first)
                if (before.isNotBlank()) result.add(NoticeRichTextSegment.Text(before))
            }
            val url = match.value.trimEnd { it == '.' || it == ',' || it == ';' }
            result.add(NoticeRichTextSegment.Link(label = url, url = url))
            cursor = match.range.last + 1
        }
        if (cursor < text.length) {
            val tail = text.substring(cursor)
            if (tail.isNotBlank()) result.add(NoticeRichTextSegment.Text(tail))
        }
        return if (result.isEmpty()) listOf(NoticeRichTextSegment.Text(text)) else result
    }

    private fun mergeAdjacentText(segments: List<NoticeRichTextSegment>): List<NoticeRichTextSegment> {
        if (segments.isEmpty()) return segments
        val merged = mutableListOf<NoticeRichTextSegment>()
        for (segment in segments) {
            when (segment) {
                is NoticeRichTextSegment.Text -> {
                    val last = merged.lastOrNull()
                    if (last is NoticeRichTextSegment.Text) {
                        merged[merged.lastIndex] = NoticeRichTextSegment.Text(last.value + segment.value)
                    } else {
                        merged.add(segment)
                    }
                }
                is NoticeRichTextSegment.Link -> merged.add(segment)
            }
        }
        return merged.filter {
            it !is NoticeRichTextSegment.Text || it.value.isNotBlank()
        }
    }

    private fun stripInnerTags(value: String): String =
        value.replace(Regex("<[^>]+>"), "")
}
