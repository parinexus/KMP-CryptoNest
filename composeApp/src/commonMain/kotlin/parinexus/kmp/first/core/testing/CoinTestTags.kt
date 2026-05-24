package parinexus.kmp.first.core.testing

object CoinTestTags {
    const val COINS_DASHBOARD_TITLE = "coins_dashboard_title"
    const val COIN_CHART_DIALOG = "coin_chart_dialog"
    const val COIN_CHART_CLOSE = "coin_chart_close"

    fun coinGridItem(coinId: String): String = "coin_grid_item_$coinId"
}
