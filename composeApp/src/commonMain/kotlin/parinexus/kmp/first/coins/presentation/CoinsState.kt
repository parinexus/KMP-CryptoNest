package parinexus.kmp.first.coins.presentation

import androidx.compose.runtime.Stable
import org.jetbrains.compose.resources.StringResource

@Stable
data class CoinsState(
    val isLoading: Boolean = false,
    val error: StringResource? = null,
    val coins: List<CoinUiModel> = emptyList(),
    val chartState: UiChartState? = null
)

@Stable
data class UiChartState(
    val sparkLine: List<Double> = emptyList(),
    val isLoading: Boolean = false,
    val coinName: String = "",
)