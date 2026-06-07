package parinexus.kmp.first.coins.domain.model

import parinexus.kmp.first.core.util.NoticeRichTextSegment

data class CoinNoticeModel(
    val type: String,
    val segments: List<NoticeRichTextSegment>,
)
