package parinexus.kmp.first.coins.presentation.detail

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import parinexus.kmp.first.core.util.NoticeRichTextSegment

@Composable
fun CoinNoticeText(
    segments: List<NoticeRichTextSegment>,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    linkColor: Color = MaterialTheme.colorScheme.primary,
) {
    val annotated = buildAnnotatedString {
        segments.forEach { segment ->
            when (segment) {
                is NoticeRichTextSegment.Text -> append(segment.value)
                is NoticeRichTextSegment.Link -> {
                    pushLink(LinkAnnotation.Url(segment.url))
                    withStyle(
                        SpanStyle(
                            color = linkColor,
                            textDecoration = TextDecoration.Underline,
                        ),
                    ) {
                        append(segment.label)
                    }
                    pop()
                }
            }
        }
    }

    Text(
        text = annotated,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        color = textColor,
    )
}
