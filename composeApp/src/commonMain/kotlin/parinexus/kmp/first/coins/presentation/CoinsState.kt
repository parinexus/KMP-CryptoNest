package parinexus.kmp.first.coins.presentation

import androidx.compose.runtime.Stable
import org.jetbrains.compose.resources.StringResource

@Stable
data class CoinsState(
    val content: CoinsListContent = CoinsListContent.Loading,
    val chartState: UiChartState? = null,
    val cacheBanner: StringResource? = null,
    val isRefreshing: Boolean = false,
)
