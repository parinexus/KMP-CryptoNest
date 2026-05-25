package parinexus.kmp.first.coins.presentation.detail

sealed interface CoinDetailChartState {
    data object Loading : CoinDetailChartState
    data class Ready(val sparkline: List<Double>) : CoinDetailChartState
    data class Error(val message: String) : CoinDetailChartState
    data class Empty(val message: String) : CoinDetailChartState
}
