package parinexus.kmp.first.coins.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import parinexus.kmp.first.coins.domain.FetchCoinDetailsUseCase
import parinexus.kmp.first.coins.domain.FetchCoinPriceHistoryUseCase
import parinexus.kmp.first.coins.domain.FetchCoinsListUseCase
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.test.fake.FakeCoinsRemoteDataSource
import parinexus.kmp.first.test.fixture.TestCoins
import parinexus.kmp.first.test.rule.MainCoroutineRule

class CoinsListViewModelTest : MainCoroutineRule() {

    private lateinit var remote: FakeCoinsRemoteDataSource

    @BeforeTest
    fun setUpMainDispatcher() = setUp()

    @AfterTest
    fun tearDownMainDispatcher() = tearDown()

    @Test
    fun loadCoins_emitsSuccessWithMappedUiModels() = runTest {
        remote = FakeCoinsRemoteDataSource()
        val viewModel = createViewModel()

        viewModel.state.test {
            var loaded = awaitItem()
            while (loaded.coins.isEmpty() && loaded.error == null) {
                loaded = awaitItem()
            }
            assertThat(loaded.isLoading).isFalse()
            assertThat(loaded.coins).hasSize(2)
            assertThat(loaded.coins.first().name).isEqualTo("Bitcoin")
            assertThat(loaded.error).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadCoins_emitsErrorStateOnFailure() = runTest {
        remote = FakeCoinsRemoteDataSource(
            coinsResult = Result.Error(DataError.Remote.NO_INTERNET),
        )
        val viewModel = createViewModel()

        viewModel.state.test {
            var state = awaitItem()
            while (state.error == null && state.isLoading) {
                state = awaitItem()
            }
            assertThat(state.isLoading).isFalse()
            assertThat(state.coins).hasSize(0)
            assertThat(state.error).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onCoinLongPressed_loadsSortedSparkline() = runTest {
        remote = FakeCoinsRemoteDataSource()
        val viewModel = createViewModel()

        viewModel.state.test {
            var loaded = awaitItem()
            while (loaded.coins.isEmpty() && loaded.error == null) {
                loaded = awaitItem()
            }
            viewModel.onCoinLongPressed(TestCoins.BITCOIN_ID)

            var state = awaitItem()
            while (state.chartState == null || state.chartState?.isLoading == true) {
                state = awaitItem()
            }

            assertThat(state.chartState?.sparkLine).isEqualTo(
                listOf(48_000.0, 49_000.0, 50_000.0),
            )
            assertThat(remote.priceHistoryRequests).isEqualTo(listOf(TestCoins.BITCOIN_ID))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onDismissChart_clearsChartState() = runTest {
        remote = FakeCoinsRemoteDataSource()
        val viewModel = createViewModel()

        viewModel.state.test {
            var loaded = awaitItem()
            while (loaded.coins.isEmpty() && loaded.error == null) {
                loaded = awaitItem()
            }
            viewModel.onCoinLongPressed(TestCoins.BITCOIN_ID)

            var state = awaitItem()
            while (state.chartState?.isLoading != false) {
                state = awaitItem()
            }

            viewModel.onDismissChart()
            val cleared = awaitItem()
            assertThat(cleared.chartState).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createViewModel(): CoinsListViewModel {
        return CoinsListViewModel(
            fetchCoinsListUseCase = FetchCoinsListUseCase(remote),
            fetchCoinPriceHistoryUseCase = FetchCoinPriceHistoryUseCase(remote),
        )
    }
}
