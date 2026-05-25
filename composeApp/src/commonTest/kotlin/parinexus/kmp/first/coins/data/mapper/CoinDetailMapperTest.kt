package parinexus.kmp.first.coins.data.mapper

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import kotlin.test.Test
import parinexus.kmp.first.test.fixture.TestCoins

class CoinDetailMapperTest {

    @Test
    fun toCoinDetailModel_mapsAllFields() {
        val model = TestCoins.bitcoinDetailDto.toCoinDetailModel()

        assertThat(model.coin.id).isEqualTo(TestCoins.BITCOIN_ID)
        assertThat(model.coin.name).isEqualTo("Bitcoin")
        assertThat(model.price).isEqualTo(50_000.0)
        assertThat(model.changePercent).isEqualTo(2.5)
        assertThat(model.rank).isEqualTo(1)
        assertThat(model.marketCap).isEqualTo(1_000_000_000_000.0)
        assertThat(model.sparkline).hasSize(3)
        assertThat(model.tags).hasSize(1)
        assertThat(model.description).isEqualTo("Bitcoin is the first decentralized digital currency.")
    }

    @Test
    fun toCoinInfoModel_preservesTradeFields() {
        val info = TestCoins.bitcoinDetailDto.toCoinDetailModel().toCoinInfoModel()

        assertThat(info.coin.id).isEqualTo(TestCoins.BITCOIN_ID)
        assertThat(info.price).isEqualTo(50_000.0)
    }
}
