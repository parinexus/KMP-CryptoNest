package parinexus.kmp.first.trade.data.mapper

import kotlinx.datetime.Clock
import parinexus.kmp.first.core.domain.coin.Coin
import parinexus.kmp.first.trade.data.local.TradeRecordEntity
import parinexus.kmp.first.trade.domain.model.TradeRecord
import parinexus.kmp.first.trade.domain.model.TradeRecordDraft
import parinexus.kmp.first.trade.domain.model.TradeType

fun TradeRecordEntity.toTradeRecord(): TradeRecord = TradeRecord(
    id = id,
    coin = Coin(
        id = coinId,
        name = coinName,
        symbol = coinSymbol,
        iconUrl = coinIconUrl,
    ),
    type = type.toTradeType(),
    amountInFiat = amountInFiat,
    amountInUnit = amountInUnit,
    priceAtTrade = priceAtTrade,
    executedAtEpochMs = executedAtEpochMs,
)

fun TradeRecordDraft.toTradeRecordEntity(
    executedAtEpochMs: Long = Clock.System.now().toEpochMilliseconds(),
): TradeRecordEntity = TradeRecordEntity(
    coinId = coin.id,
    coinName = coin.name,
    coinSymbol = coin.symbol,
    coinIconUrl = coin.iconUrl,
    type = type.name,
    amountInFiat = amountInFiat,
    amountInUnit = amountInUnit,
    priceAtTrade = priceAtTrade,
    executedAtEpochMs = executedAtEpochMs,
)

private fun String.toTradeType(): TradeType =
    TradeType.valueOf(this)
