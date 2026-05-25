package parinexus.kmp.first.core.testing

object CoinTestTags {
    const val COINS_DASHBOARD_TITLE = "coins_dashboard_title"
    const val COINS_LIST_INTERACTION_HINT = "coins_list_interaction_hint"
    const val COINS_GRID_HOLD_HINT = "coins_grid_hold_hint"
    const val COINS_LOADING = "coins_loading"
    const val COINS_ERROR = "coins_error"
    const val COINS_EMPTY = "coins_empty"
    const val COINS_RETRY = "coins_retry"
    const val COIN_CHART_DIALOG = "coin_chart_dialog"
    const val COIN_CHART_CLOSE = "coin_chart_close"

    fun coinGridItem(coinId: String): String = "coin_grid_item_$coinId"

    const val COIN_DETAIL_TITLE = "coin_detail_title"
    const val COIN_DETAIL_BACK = "coin_detail_back"
    const val COIN_DETAIL_LOADING = "coin_detail_loading"
    const val COIN_DETAIL_ERROR = "coin_detail_error"
    const val COIN_DETAIL_SUCCESS = "coin_detail_success"
    const val COIN_DETAIL_ICON = "coin_detail_icon"
    const val COIN_DETAIL_NAME = "coin_detail_name"
    const val COIN_DETAIL_PRICE = "coin_detail_price"
    const val COIN_DETAIL_NOTICE = "coin_detail_notice"
    const val COIN_DETAIL_CHART = "coin_detail_chart"
    const val COIN_DETAIL_CHART_ERROR = "coin_detail_chart_error"
    const val COIN_DETAIL_DESCRIPTION = "coin_detail_description"
    const val COIN_DETAIL_BUY = "coin_detail_buy"
    const val COIN_DETAIL_SELL = "coin_detail_sell"
}
