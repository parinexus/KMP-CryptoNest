package parinexus.kmp.first.trade.domain

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.test.fake.FakePortfolioRepository
import parinexus.kmp.first.test.fixture.TestCoins
import parinexus.kmp.first.test.fixture.TestPortfolio

class SellCoinUseCaseTest {

    @Test
    fun sellCoin_returnsInsufficientFundsWhenNotOwned() = runTest {
        val repository = FakePortfolioRepository()
        val useCase = SellCoinUseCase(repository)

        val result = useCase.sellCoin(
            coin = TestCoins.bitcoin,
            amountInFiat = 100.0,
            price = 50_000.0,
        )

        assertThat(result).isEqualTo(Result.Error(DataError.Local.INSUFFICIENT_FUNDS))
    }

    @Test
    fun sellCoin_returnsInsufficientFundsWhenSellingTooMuch() = runTest {
        val repository = FakePortfolioRepository(
            ownedCoins = mapOf(
                TestCoins.BITCOIN_ID to TestPortfolio.bitcoinHolding(
                    ownedAmountInUnit = 0.001,
                    ownedAmountInFiat = 50.0,
                ),
            ),
        )
        val useCase = SellCoinUseCase(repository)

        val result = useCase.sellCoin(
            coin = TestCoins.bitcoin,
            amountInFiat = 500.0,
            price = 50_000.0,
        )

        assertThat(result).isEqualTo(Result.Error(DataError.Local.INSUFFICIENT_FUNDS))
    }

    @Test
    fun sellCoin_partialSellUpdatesHoldingAndIncreasesCash() = runTest {
        val repository = FakePortfolioRepository(
            cashBalance = 1_000.0,
            ownedCoins = mapOf(
                TestCoins.BITCOIN_ID to TestPortfolio.bitcoinHolding(
                    ownedAmountInUnit = 0.02,
                    ownedAmountInFiat = 1_000.0,
                    averagePurchasePrice = 50_000.0,
                ),
            ),
        )
        val useCase = SellCoinUseCase(repository)

        val result = useCase.sellCoin(
            coin = TestCoins.bitcoin,
            amountInFiat = 500.0,
            price = 50_000.0,
        )

        assertThat(result is Result.Success).isTrue()
        assertThat(repository.removedCoinIds).hasSize(0)
        assertThat(repository.insertedCoins.last().ownedAmountInFiat).isEqualTo(500.0)
        assertThat(repository.updatedCashBalances.last()).isEqualTo(1_500.0)
    }

    @Test
    fun sellCoin_fullSellRemovesCoinFromPortfolio() = runTest {
        val repository = FakePortfolioRepository(
            cashBalance = 0.0,
            ownedCoins = mapOf(
                TestCoins.BITCOIN_ID to TestPortfolio.bitcoinHolding(
                    ownedAmountInUnit = 0.02,
                    ownedAmountInFiat = 1_000.0,
                ),
            ),
        )
        val useCase = SellCoinUseCase(repository)

        val result = useCase.sellCoin(
            coin = TestCoins.bitcoin,
            amountInFiat = 1_000.0,
            price = 50_000.0,
        )

        assertThat(result is Result.Success).isTrue()
        assertThat(repository.removedCoinIds).contains(TestCoins.BITCOIN_ID)
    }

    @Test
    fun sellCoin_propagatesPortfolioLookupError() = runTest {
        val repository = FakePortfolioRepository().apply {
            setPortfolioLookupResult(Result.Error(DataError.Remote.SERVER))
        }
        val useCase = SellCoinUseCase(repository)

        val result = useCase.sellCoin(
            coin = TestCoins.bitcoin,
            amountInFiat = 100.0,
            price = 50_000.0,
        )

        assertThat(result).isEqualTo(Result.Error(DataError.Remote.SERVER))
    }
}
