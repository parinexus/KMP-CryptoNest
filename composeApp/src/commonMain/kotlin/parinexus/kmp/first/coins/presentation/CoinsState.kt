package parinexus.kmp.first.coins.presentation

import androidx.compose.runtime.Stable

@Stable
data class CoinsState(
    val content: CoinsListContent = CoinsListContent.Loading,
    val chartState: UiChartState? = null,
)

@Stable
data class UiChartState(
    val sparkLine: List<Double> = emptyList(),
    val isLoading: Boolean = false,
    val coinName: String = "",
    val errorMessage: String? = null,
)
