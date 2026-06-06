package parinexus.kmp.first.test.fixture

import parinexus.kmp.first.trade.domain.model.TradeRecord
import parinexus.kmp.first.trade.domain.model.TradeRecordDraft
import parinexus.kmp.first.trade.domain.model.TradeType

object TestTrades {

    val bitcoinBuyDraft = TradeRecordDraft(
        coin = TestCoins.bitcoin,
        type = TradeType.BUY,
        amountInFiat = 500.0,
        amountInUnit = 0.01,
        priceAtTrade = 50_000.0,
    )

    fun bitcoinBuyRecord(id: Long = 1L) = TradeRecord(
        id = id,
        coin = TestCoins.bitcoin,
        type = TradeType.BUY,
        amountInFiat = 500.0,
        amountInUnit = 0.01,
        priceAtTrade = 50_000.0,
        executedAtEpochMs = 1_700_000_000_000L,
    )
}
