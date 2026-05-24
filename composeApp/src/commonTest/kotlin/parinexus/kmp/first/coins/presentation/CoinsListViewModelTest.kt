package parinexus.kmp.first.coins.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
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
            var state = awaitItem()
            while (state.content !is CoinsListContent.Success) {
                state = awaitItem()
            }
            val success = state.content as CoinsListContent.Success
            assertThat(success.coins).hasSize(2)
            assertThat(success.coins.first().name).isEqualTo("Bitcoin")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadCoins_emitsErrorStateOnFailure() = runTest {
        remote = FakeCoinsRemoteDataSource(
            coinsResult = Result.Error(
                error = DataError.Remote.NO_INTERNET,
                message = "No internet",
            ),
        )
        val viewModel = createViewModel()

        viewModel.state.test {
            var state = awaitItem()
            while (state.content !is CoinsListContent.Error) {
                state = awaitItem()
            }
            val error = state.content as CoinsListContent.Error
            assertThat(error.message).isEqualTo("No internet")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onCoinLongPressed_loadsSortedSparkline() = runTest {
        remote = FakeCoinsRemoteDataSource()
        val viewModel = createViewModel()

        viewModel.state.test {
            var state = awaitItem()
            while (state.content !is CoinsListContent.Success) {
                state = awaitItem()
            }
            viewModel.onCoinLongPressed(TestCoins.BITCOIN_ID)

            state = awaitItem()
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
            var state = awaitItem()
            while (state.content !is CoinsListContent.Success) {
                state = awaitItem()
            }
            viewModel.onCoinLongPressed(TestCoins.BITCOIN_ID)

            state = awaitItem()
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
