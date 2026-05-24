package parinexus.kmp.first.portfolio.data.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test
import parinexus.kmp.first.portfolio.data.local.PortfolioCoinEntity
import parinexus.kmp.first.test.fixture.TestCoins
import parinexus.kmp.first.test.fixture.TestPortfolio

class PortfolioCoinEntityMapperTest {

    @Test
    fun toPortfolioCoinModel_calculatesPerformanceAndFiatValue() {
        val entity = PortfolioCoinEntity(
            coinId = TestCoins.BITCOIN_ID,
            name = "Bitcoin",
            symbol = "BTC",
            iconUrl = TestCoins.bitcoin.iconUrl,
            averagePurchasePrice = 40_000.0,
            amountOwned = 0.5,
            timestamp = 0L,
        )

        val model = entity.toPortfolioCoinModel(currentPrice = 44_000.0)

        assertThat(model.performancePercent).isEqualTo(10.0)
        assertThat(model.ownedAmountInFiat).isEqualTo(22_000.0)
        assertThat(model.ownedAmountInUnit).isEqualTo(0.5)
    }

    @Test
    fun toPortfolioCoinEntity_roundTripsCoreFields() {
        val model = TestPortfolio.bitcoinHolding()

        val entity = model.toPortfolioCoinEntity()

        assertThat(entity.coinId).isEqualTo(TestCoins.BITCOIN_ID)
        assertThat(entity.amountOwned).isEqualTo(model.ownedAmountInUnit)
        assertThat(entity.averagePurchasePrice).isEqualTo(model.averagePurchasePrice)
    }
}
