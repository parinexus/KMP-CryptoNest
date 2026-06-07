package parinexus.kmp.first.trade.presentation.history

import parinexus.kmp.first.core.util.formatCoinUnit
import parinexus.kmp.first.core.util.formatFiat
import parinexus.kmp.first.core.util.formatTradeTimestamp
import parinexus.kmp.first.trade.domain.model.TradeRecord
import parinexus.kmp.first.trade.domain.model.TradeType

fun TradeRecord.toUiTradeHistoryItem(): UiTradeHistoryItem {
    val isBuy = type == TradeType.BUY
    return UiTradeHistoryItem(
        id = id,
        coinId = coin.id,
        coinName = coin.name,
        coinSymbol = coin.symbol,
        coinIconUrl = coin.iconUrl,
        typeLabel = if (isBuy) "Buy" else "Sell",
        isBuy = isBuy,
        amountInFiatText = formatFiat(amountInFiat),
        amountInUnitText = formatCoinUnit(amountInUnit, coin.symbol),
        priceAtTradeText = formatFiat(priceAtTrade),
        executedAtText = formatTradeTimestamp(executedAtEpochMs),
    )
}
