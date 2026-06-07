package parinexus.kmp.first.trade.data.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test
import parinexus.kmp.first.test.fixture.TestCoins
import parinexus.kmp.first.test.fixture.TestTrades
import parinexus.kmp.first.trade.data.local.TradeRecordEntity
import parinexus.kmp.first.trade.domain.model.TradeType

class TradeRecordMapperTest {

    @Test
    fun entity_toTradeRecord_mapsAllFields() {
        val entity = TradeRecordEntity(
            id = 7L,
            coinId = TestCoins.BITCOIN_ID,
            coinName = "Bitcoin",
            coinSymbol = "BTC",
            coinIconUrl = TestCoins.bitcoin.iconUrl,
            type = TradeType.SELL.name,
            amountInFiat = 200.0,
            amountInUnit = 0.004,
            priceAtTrade = 50_000.0,
            executedAtEpochMs = 1_234L,
        )

        val record = entity.toTradeRecord()

        assertThat(record.id).isEqualTo(7L)
        assertThat(record.coin.id).isEqualTo(TestCoins.BITCOIN_ID)
        assertThat(record.type).isEqualTo(TradeType.SELL)
        assertThat(record.amountInFiat).isEqualTo(200.0)
        assertThat(record.executedAtEpochMs).isEqualTo(1_234L)
    }

    @Test
    fun draft_toTradeRecordEntity_usesProvidedTimestamp() {
        val entity = TestTrades.bitcoinBuyDraft.toTradeRecordEntity(
            executedAtEpochMs = 9_999L,
        )

        assertThat(entity.type).isEqualTo("BUY")
        assertThat(entity.coinId).isEqualTo(TestCoins.BITCOIN_ID)
        assertThat(entity.executedAtEpochMs).isEqualTo(9_999L)
    }
}
