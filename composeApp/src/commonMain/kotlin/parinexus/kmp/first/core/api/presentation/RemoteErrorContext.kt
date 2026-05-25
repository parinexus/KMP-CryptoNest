package parinexus.kmp.first.core.api.presentation

/**
 * Identifies which screen or request failed so rate-limit and other errors
 * can be shown with a clear, distinct message.
 */
enum class RemoteErrorContext {
    CoinsList,
    CoinDetail,
    CoinDetailChart,
    CoinPriceChartDialog,
}
