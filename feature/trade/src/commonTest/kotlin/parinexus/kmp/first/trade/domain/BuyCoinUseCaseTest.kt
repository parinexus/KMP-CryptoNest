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
import parinexus.kmp.first.test.fake.FakeTradePortfolioWriter
import parinexus.kmp.first.test.fixture.TestCoins
import parinexus.kmp.first.test.fixture.TestPortfolio
import parinexus.kmp.first.trade.domain.model.TradeType

class BuyCoinUseCaseTest {

    private fun createUseCase(
        portfolio: FakePortfolioRepository,
        tradeHistory: FakeTradeHistoryRepository = FakeTradeHistoryRepository(),
    ): BuyCoinUseCase {
        val writer = FakeTradePortfolioWriter(portfolio, tradeHistory)
        return BuyCoinUseCase(
            portfolioRepository = portfolio,
            tradePortfolioWriter = writer,
        )
    }

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
        assertThat(repository.appliedBuyHoldings).hasSize(0)
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
        assertThat(repository.appliedBuyHoldings).hasSize(1)
        assertThat(repository.appliedBuyHoldings.first().ownedAmountInUnit).isEqualTo(0.02)
        assertThat(repository.updatedCashBalances.last()).isEqualTo(9_000.0)
        assertThat(tradeHistory.recordedDrafts.first().type).isEqualTo(TradeType.BUY)
    }

    @Test
    fun buyCoin_updatesAveragePriceForExistingHolding() = runTest {
        val existing = TestPortfolio.bitcoinPortfolioHolding(
            ownedAmountInUnit = 0.01,
            averagePurchasePrice = 40_000.0,
        )
        val repository = FakePortfolioRepository(
            cashBalance = 10_000.0,
            holdings = mapOf(TestCoins.BITCOIN_ID to existing),
        )
        val useCase = createUseCase(repository)

        val result = useCase.buyCoin(
            coin = TestCoins.bitcoin,
            amountInFiat = 600.0,
            price = 50_000.0,
        )

        assertThat(result is Result.Success).isTrue()
        val updated = repository.appliedBuyHoldings.last()
        assertThat(updated.ownedAmountInUnit).isEqualTo(0.01 + 0.012)
        assertThat(updated.averagePurchasePrice).isEqualTo(1_000.0 / updated.ownedAmountInUnit)
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
