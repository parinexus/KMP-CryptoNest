package parinexus.kmp.first.core.testing

object CoinTestTags {
    const val COINS_DASHBOARD_TITLE = "coins_dashboard_title"
    const val COINS_LOADING = "coins_loading"
    const val COINS_ERROR = "coins_error"
    const val COINS_EMPTY = "coins_empty"
    const val COINS_RETRY = "coins_retry"
    const val COIN_CHART_DIALOG = "coin_chart_dialog"
    const val COIN_CHART_CLOSE = "coin_chart_close"

    fun coinGridItem(coinId: String): String = "coin_grid_item_$coinId"
}
