package parinexus.kmp.first.trade.presentation.history

data class TradeHistoryState(
    val trades: List<UiTradeHistoryItem> = emptyList(),
    val isLoading: Boolean = true,
)

data class UiTradeHistoryItem(
    val id: Long,
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val coinIconUrl: String,
    val typeLabel: String,
    val isBuy: Boolean,
    val amountInFiatText: String,
    val amountInUnitText: String,
    val priceAtTradeText: String,
    val executedAtText: String,
)
