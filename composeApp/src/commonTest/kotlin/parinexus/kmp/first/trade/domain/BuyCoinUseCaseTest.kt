package parinexus.kmp.first.trade.domain

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.test.fake.FakePortfolioRepository
import parinexus.kmp.first.test.fake.FakeTradeHistoryRepository
import parinexus.kmp.first.test.fixture.TestCoins
import parinexus.kmp.first.test.fixture.TestPortfolio
import parinexus.kmp.first.trade.domain.model.TradeType

class BuyCoinUseCaseTest {

    private fun createUseCase(
        portfolio: FakePortfolioRepository,
        tradeHistory: FakeTradeHistoryRepository = FakeTradeHistoryRepository(),
    ) = BuyCoinUseCase(
        portfolioRepository = portfolio,
        recordTradeUseCase = RecordTradeUseCase(tradeHistory),
    )

    @Test
    fun buyCoin_returnsInsufficientFundsWhenBalanceTooLow() = runTest {
        val repository = FakePortfolioRepository(cashBalance = 100.0)
        val useCase = createUseCase(repository)

        val result = useCase.buyCoin(
            coin = TestCoins.bitcoin,
            amountInFiat = 500.0,
            price = 50_000.0,
        )

        assertThat(result).isEqualTo(Result.Error(DataError.Local.INSUFFICIENT_FUNDS))
        assertThat(repository.insertedCoins).hasSize(0)
    }

    @Test
    fun buyCoin_insertsNewHoldingAndDeductsCash() = runTest {
        val repository = FakePortfolioRepository(cashBalance = 10_000.0)
        val tradeHistory = FakeTradeHistoryRepository()
        val useCase = createUseCase(repository, tradeHistory)

        val result = useCase.buyCoin(
            coin = TestCoins.bitcoin,
            amountInFiat = 1_000.0,
            price = 50_000.0,
        )

        assertThat(result is Result.Success).isTrue()
        assertThat(repository.insertedCoins).hasSize(1)
        assertThat(repository.insertedCoins.first().ownedAmountInUnit).isEqualTo(0.02)
        assertThat(repository.updatedCashBalances.last()).isEqualTo(9_000.0)
        assertThat(tradeHistory.recordedDrafts).hasSize(1)
        assertThat(tradeHistory.recordedDrafts.first().type).isEqualTo(TradeType.BUY)
    }

    @Test
    fun buyCoin_updatesAveragePriceForExistingHolding() = runTest {
        val existing = TestPortfolio.bitcoinHolding(
            ownedAmountInUnit = 0.01,
            ownedAmountInFiat = 400.0,
            averagePurchasePrice = 40_000.0,
        )
        val repository = FakePortfolioRepository(
            cashBalance = 10_000.0,
            ownedCoins = mapOf(TestCoins.BITCOIN_ID to existing),
        )
        val useCase = createUseCase(repository)

        val result = useCase.buyCoin(
            coin = TestCoins.bitcoin,
            amountInFiat = 600.0,
            price = 50_000.0,
        )

        assertThat(result is Result.Success).isTrue()
        val updated = repository.insertedCoins.last()
        assertThat(updated.ownedAmountInUnit).isEqualTo(0.01 + 0.012)
        assertThat(updated.ownedAmountInFiat).isEqualTo(1_000.0)
        assertThat(updated.averagePurchasePrice).isEqualTo(1_000.0 / updated.ownedAmountInUnit)
    }

    @Test
    fun buyCoin_propagatesPortfolioLookupError() = runTest {
        val repository = FakePortfolioRepository(cashBalance = 10_000.0).apply {
            setPortfolioLookupResult(Result.Error(DataError.Remote.NO_INTERNET))
        }
        val useCase = createUseCase(repository)

        val result = useCase.buyCoin(
            coin = TestCoins.bitcoin,
            amountInFiat = 100.0,
            price = 50_000.0,
        )

        assertThat(result).isEqualTo(Result.Error(DataError.Remote.NO_INTERNET))
    }

    @Test
    fun buyCoin_doesNotRecordTradeWhenInsufficientFunds() = runTest {
        val tradeHistory = FakeTradeHistoryRepository()
        val useCase = createUseCase(
            FakePortfolioRepository(cashBalance = 10.0),
            tradeHistory,
        )

        useCase.buyCoin(TestCoins.bitcoin, amountInFiat = 500.0, price = 50_000.0)

        assertThat(tradeHistory.recordedDrafts).hasSize(0)
    }
}
