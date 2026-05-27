package parinexus.kmp.first.coins.presentation.detail

import org.jetbrains.compose.resources.StringResource

sealed interface CoinDetailContent {
    data object Loading : CoinDetailContent
    data class Success(val detail: CoinDetailUiModel) : CoinDetailContent
    data class Error(val message: String) : CoinDetailContent
}

data class CoinDetailState(
    val content: CoinDetailContent = CoinDetailContent.Loading,
    val chartState: CoinDetailChartState = CoinDetailChartState.Loading,
    val cacheBanner: StringResource? = null,
    val isRefreshing: Boolean = false,
)
