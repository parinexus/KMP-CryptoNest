package parinexus.kmp.first.coins.data.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test
import parinexus.kmp.first.test.fixture.TestCoins

class CoinModelMapperTest {

    @Test
    fun toCoinInfoModel_mapsAllFields() {
        val dto = TestCoins.bitcoinDto

        val model = dto.toCoinInfoModel()

        assertThat(model.coin.id).isEqualTo(TestCoins.BITCOIN_ID)
        assertThat(model.coin.name).isEqualTo("Bitcoin")
        assertThat(model.coin.symbol).isEqualTo("BTC")
        assertThat(model.price).isEqualTo(50_000.0)
        assertThat(model.changePercent).isEqualTo(2.5)
    }

    @Test
    fun toPriceModel_usesZeroWhenPriceIsNull() {
        val model = TestCoins.priceHistoryResponse.data.history.first()
            .copy(price = null)
            .toPriceModel()

        assertThat(model.price).isEqualTo(0.0)
        assertThat(model.timestamp).isEqualTo(1L)
    }
}
