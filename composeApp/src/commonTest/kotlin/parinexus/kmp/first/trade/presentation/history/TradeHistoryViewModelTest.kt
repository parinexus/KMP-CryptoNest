package parinexus.kmp.first.trade.presentation.history

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import parinexus.kmp.first.test.fake.FakeTradeHistoryRepository
import parinexus.kmp.first.test.fixture.TestTrades
import parinexus.kmp.first.test.rule.MainCoroutineRule
import parinexus.kmp.first.trade.domain.ObserveTradeHistoryUseCase

class TradeHistoryViewModelTest : MainCoroutineRule() {

    @BeforeTest
    fun setUpMainDispatcher() = setUp()

    @AfterTest
    fun tearDownMainDispatcher() = tearDown()

    @Test
    fun state_emitsMappedTradesFromRepository() = runTest {
        val repository = FakeTradeHistoryRepository(
            initialTrades = listOf(TestTrades.bitcoinBuyRecord()),
        )
        val viewModel = TradeHistoryViewModel(ObserveTradeHistoryUseCase(repository))

        viewModel.state.test {
            val loaded = awaitItem()
            assertThat(loaded.isLoading).isFalse()
            assertThat(loaded.trades).hasSize(1)
            assertThat(loaded.trades.first().coinName).isEqualTo("Bitcoin")
            assertThat(loaded.trades.first().typeLabel).isEqualTo("Buy")
            assertThat(loaded.trades.first().isBuy).isEqualTo(true)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun state_emptyList_showsNoTrades() = runTest {
        val viewModel = TradeHistoryViewModel(
            ObserveTradeHistoryUseCase(FakeTradeHistoryRepository()),
        )

        viewModel.state.test {
            val loaded = awaitItem()
            assertThat(loaded.isLoading).isFalse()
            assertThat(loaded.trades).hasSize(0)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
