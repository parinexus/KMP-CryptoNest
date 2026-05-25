package parinexus.kmp.first.core.util

/**
 * Converts Coinranking HTML snippets (notices, etc.) into readable plain text for Compose UI.
 */
object HtmlContentSanitizer {

    private val anchorTag = Regex("""<a\s[^>]*>([^<]*)</a>""", RegexOption.IGNORE_CASE)
    private val breakTag = Regex("""<br\s*/?>""", RegexOption.IGNORE_CASE)
    private val anyTag = Regex("<[^>]+>")

    fun toPlainText(html: String): String {
        if (html.isBlank()) return ""
        if (!html.contains('<')) return html.trim()

        var text = html
        text = breakTag.replace(text, "\n")
        text = anchorTag.replace(text) { match -> match.groupValues[1].trim() }
        text = anyTag.replace(text, "")
        text = decodeEntitiesPublic(text)
        return text
            .lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .joinToString(" ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    fun decodeEntitiesPublic(value: String): String = decodeEntities(value)

    private fun decodeEntities(value: String): String = value
        .replace("&nbsp;", " ")
        .replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
}
