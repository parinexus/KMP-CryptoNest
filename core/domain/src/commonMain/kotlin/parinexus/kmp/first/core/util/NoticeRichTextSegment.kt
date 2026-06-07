package parinexus.kmp.first.core.util

sealed interface NoticeRichTextSegment {
    data class Text(val value: String) : NoticeRichTextSegment
    data class Link(val label: String, val url: String) : NoticeRichTextSegment
}
