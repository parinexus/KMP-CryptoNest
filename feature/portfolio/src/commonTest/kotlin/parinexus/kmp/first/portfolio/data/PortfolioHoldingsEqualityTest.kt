package parinexus.kmp.first.portfolio.data

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import kotlin.test.Test
import parinexus.kmp.first.portfolio.data.local.PortfolioCoinEntity
import parinexus.kmp.first.test.fixture.TestCoins

class PortfolioHoldingsEqualityTest {

    private fun entity(
        coinId: String = TestCoins.BITCOIN_ID,
        amountOwned: Double = 1.0,
        averagePurchasePrice: Double = 50_000.0,
    ) = PortfolioCoinEntity(
        coinId = coinId,
        name = TestCoins.bitcoin.name,
        symbol = TestCoins.bitcoin.symbol,
        iconUrl = TestCoins.bitcoin.iconUrl,
        amountOwned = amountOwned,
        averagePurchasePrice = averagePurchasePrice,
        timestamp = 1L,
    )

    @Test
    fun sameHoldingsAs_returnsTrueForIdenticalLists() {
        val holdings = listOf(entity(), entity(coinId = TestCoins.ETHEREUM_ID, amountOwned = 2.0))

        assertThat(holdings.sameHoldingsAs(holdings)).isTrue()
    }

    @Test
    fun sameHoldingsAs_returnsFalseWhenAmountChanges() {
        val before = listOf(entity(amountOwned = 1.0))
        val after = listOf(entity(amountOwned = 2.0))

        assertThat(before.sameHoldingsAs(after)).isFalse()
    }

    @Test
    fun sameHoldingsAs_returnsFalseWhenSizeDiffers() {
        val single = listOf(entity())
        val pair = listOf(entity(), entity(coinId = TestCoins.ETHEREUM_ID))

        assertThat(single.sameHoldingsAs(pair)).isFalse()
    }
}
