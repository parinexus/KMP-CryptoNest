package parinexus.kmp.first.coins.presentation.detail

import parinexus.kmp.first.core.util.NoticeRichTextSegment

enum class CoinNoticeStyle {
    Info,
    Warning,
    Alert,
}

data class CoinNoticeUiModel(
    val segments: List<NoticeRichTextSegment>,
    val style: CoinNoticeStyle,
)
