package parinexus.kmp.first.trade.domain.model

import parinexus.kmp.first.core.domain.coin.Coin

data class TradeRecord(
    val id: Long,
    val coin: Coin,
    val type: TradeType,
    val amountInFiat: Double,
    val amountInUnit: Double,
    val priceAtTrade: Double,
    val executedAtEpochMs: Long,
)

data class TradeRecordDraft(
    val coin: Coin,
    val type: TradeType,
    val amountInFiat: Double,
    val amountInUnit: Double,
    val priceAtTrade: Double,
)
