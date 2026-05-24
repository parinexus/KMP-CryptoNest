package parinexus.kmp.first.trade.presentation.buy

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import parinexus.kmp.first.coins.domain.FetchCoinDetailsUseCase
import parinexus.kmp.first.test.fake.FakeCoinsRemoteDataSource
import parinexus.kmp.first.test.fake.FakePortfolioRepository
import parinexus.kmp.first.test.fixture.TestCoins
import parinexus.kmp.first.test.rule.MainCoroutineRule
import parinexus.kmp.first.trade.domain.BuyCoinUseCase

class BuyViewModelTest : MainCoroutineRule() {

    @BeforeTest
    fun setUpMainDispatcher() = setUp()

    @AfterTest
    fun tearDownMainDispatcher() = tearDown()

    @Test
    fun state_loadsCoinDetailsAndAvailableBalance() = runTest {
        val viewModel = createViewModel(cashBalance = 7_500.0)

        viewModel.state.test {
            var loaded = awaitItem()
            while (loaded.coin == null && loaded.error == null) {
                loaded = awaitItem()
            }
            assertThat(loaded.coin?.name).isEqualTo("Bitcoin")
            assertThat(loaded.availableAmount).contains("7,500")
            assertThat(loaded.error).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onBuyClicked_insufficientFunds_setsError() = runTest {
        val viewModel = createViewModel(cashBalance = 10.0)

        viewModel.state.test {
            var loaded = awaitItem()
            while (loaded.coin == null && loaded.error == null) {
                loaded = awaitItem()
            }
            viewModel.onAmountChanged("5000")
            viewModel.onBuyClicked()

            var state = awaitItem()
            while (state.error == null) {
                state = awaitItem()
            }
            assertThat(state.error).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onBuyClicked_success_emitsBuySuccessEvent() = runTest {
        val viewModel = createViewModel(cashBalance = 10_000.0)

        viewModel.state.test {
            var loaded = awaitItem()
            while (loaded.coin == null && loaded.error == null) {
                loaded = awaitItem()
            }
            viewModel.onAmountChanged("100")
            viewModel.onBuyClicked()
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.events.test {
            assertThat(awaitItem()).isEqualTo(BuyEvents.BuySuccess)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createViewModel(cashBalance: Double): BuyViewModel {
        val remote = FakeCoinsRemoteDataSource()
        val repository = FakePortfolioRepository(cashBalance = cashBalance)
        return BuyViewModel(
            getCoinDetailsUseCase = FetchCoinDetailsUseCase(remote),
            portfolioRepository = repository,
            buyCoinUseCase = BuyCoinUseCase(repository),
            coinId = TestCoins.BITCOIN_ID,
        )
    }
}
