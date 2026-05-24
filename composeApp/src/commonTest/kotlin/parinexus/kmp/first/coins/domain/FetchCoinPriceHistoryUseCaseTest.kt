package parinexus.kmp.first.coins.domain

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.test.fake.FakeCoinsRemoteDataSource
import parinexus.kmp.first.test.fixture.TestCoins

class FetchCoinPriceHistoryUseCaseTest {

    private val remote = FakeCoinsRemoteDataSource()
    private val useCase = FetchCoinPriceHistoryUseCase(remote)

    @Test
    fun execute_mapsPriceHistory() = runTest {
        val result = useCase.execute(TestCoins.BITCOIN_ID)

        assertThat(result is Result.Success).isTrue()
        val history = (result as Result.Success).data
        assertThat(history).hasSize(3)
        assertThat(history.first().price).isEqualTo(48_000.0)
        assertThat(history.last().timestamp).isEqualTo(3L)
    }

    @Test
    fun execute_propagatesRemoteError() = runTest {
        remote.setPriceHistoryResult(Result.Error(DataError.Remote.REQUEST_TIMEOUT))

        val result = useCase.execute(TestCoins.BITCOIN_ID)

        assertThat(result).isEqualTo(Result.Error(DataError.Remote.REQUEST_TIMEOUT))
    }
}
