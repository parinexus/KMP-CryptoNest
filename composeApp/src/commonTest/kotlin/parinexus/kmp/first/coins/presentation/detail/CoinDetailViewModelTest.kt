package parinexus.kmp.first.coins.presentation.detail

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import parinexus.kmp.first.coins.domain.FetchCoinDetailsUseCase
import parinexus.kmp.first.coins.domain.FetchCoinPriceHistoryUseCase
import parinexus.kmp.first.core.api.presentation.RemoteErrorContext
import parinexus.kmp.first.core.api.presentation.RemoteErrorUiMapper
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.test.fake.FakeCoinsRemoteDataSource
import parinexus.kmp.first.test.fixture.TestCoins
import parinexus.kmp.first.test.rule.MainCoroutineRule

class CoinDetailViewModelTest : MainCoroutineRule() {

    private lateinit var remote: FakeCoinsRemoteDataSource

    @BeforeTest
    fun setUpMainDispatcher() = setUp()

    @AfterTest
    fun tearDownMainDispatcher() = tearDown()

    @Test
    fun loadDetails_emitsSuccessWithChart() = runTest {
        remote = FakeCoinsRemoteDataSource()
        val viewModel = createViewModel(TestCoins.BITCOIN_ID)

        viewModel.state.test {
            var state = awaitItem()
            while (state.content !is CoinDetailContent.Success) {
                state = awaitItem()
            }
            val success = state.content as CoinDetailContent.Success
            assertThat(success.detail.name).isEqualTo("Bitcoin")
            assertThat(success.detail.coinId).isEqualTo(TestCoins.BITCOIN_ID)

            while (state.chartState !is CoinDetailChartState.Ready) {
                state = awaitItem()
            }
            assertThat((state.chartState as CoinDetailChartState.Ready).sparkline)
                .isEqualTo(listOf(48_000.0, 49_000.0, 50_000.0))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadDetails_emitsErrorOnFailure() = runTest {
        remote = FakeCoinsRemoteDataSource(
            coinDetailsResult = Result.Error(DataError.Remote.SERVER),
        )
        val viewModel = createViewModel(TestCoins.BITCOIN_ID)

        viewModel.state.test {
            var state = awaitItem()
            while (state.content !is CoinDetailContent.Error) {
                state = awaitItem()
            }
            assertThat(state.content).isInstanceOf(CoinDetailContent.Error::class)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun chartFailure_keepsDetailSuccess() = runTest {
        remote = FakeCoinsRemoteDataSource(
            priceHistoryResult = Result.Error(
                error = DataError.Remote.TOO_MANY_REQUESTS,
                message = "Rate limit exceeded",
            ),
        )
        val viewModel = createViewModel(TestCoins.BITCOIN_ID)

        viewModel.state.test {
            var state = awaitItem()
            while (state.content !is CoinDetailContent.Success) {
                state = awaitItem()
            }
            while (state.chartState !is CoinDetailChartState.Error) {
                state = awaitItem()
            }
            assertThat((state.content as CoinDetailContent.Success).detail.name).isEqualTo("Bitcoin")
            assertThat((state.chartState as CoinDetailChartState.Error).message)
                .isEqualTo(
                    RemoteErrorUiMapper.toDisplayMessage(
                        Result.Error(
                            error = DataError.Remote.TOO_MANY_REQUESTS,
                            message = "Rate limit exceeded",
                        ),
                        RemoteErrorContext.CoinDetailChart,
                    ),
                )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onRetry_reloadsAfterError() = runTest {
        remote = FakeCoinsRemoteDataSource(
            coinDetailsResult = Result.Error(DataError.Remote.SERVER),
        )
        val viewModel = createViewModel(TestCoins.BITCOIN_ID)

        viewModel.state.test {
            var state = awaitItem()
            while (state.content !is CoinDetailContent.Error) {
                state = awaitItem()
            }
            remote.setCoinDetailsResult(Result.Success(TestCoins.bitcoinDetailsResponse))
            viewModel.onRetry()
            while (state.content !is CoinDetailContent.Success) {
                state = awaitItem()
            }
            assertThat((state.content as CoinDetailContent.Success).detail.name).isEqualTo("Bitcoin")
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createViewModel(coinId: String): CoinDetailViewModel {
        return CoinDetailViewModel(
            fetchCoinDetailsUseCase = FetchCoinDetailsUseCase(remote),
            fetchCoinPriceHistoryUseCase = FetchCoinPriceHistoryUseCase(remote),
            coinId = coinId,
        )
    }
}
