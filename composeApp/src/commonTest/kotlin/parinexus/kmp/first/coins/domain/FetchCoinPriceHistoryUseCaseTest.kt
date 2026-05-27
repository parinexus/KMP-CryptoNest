package parinexus.kmp.first.coins.domain

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.core.domain.cache.DataFreshness
import parinexus.kmp.first.test.fake.FakeCoinsRepository
import parinexus.kmp.first.test.fake.FakeCoinsRemoteDataSource
import parinexus.kmp.first.test.fixture.TestCoins

class FetchCoinPriceHistoryUseCaseTest {

    private val remote = FakeCoinsRemoteDataSource()
    private val useCase = FetchCoinPriceHistoryUseCase(FakeCoinsRepository(remote))

    @Test
    fun execute_returnsPriceHistory() = runTest {
        val result = useCase.execute(TestCoins.BITCOIN_ID, forceRefresh = true)

        assertThat(result).isInstanceOf(Result.Success::class)
        val success = result as Result.Success
        assertThat(success.data.value).hasSize(3)
        assertThat(success.data.freshness).isEqualTo(DataFreshness.Fresh)
    }

    @Test
    fun execute_propagatesRemoteError() = runTest {
        remote.setPriceHistoryResult(Result.Error(DataError.Remote.REQUEST_TIMEOUT))

        val result = useCase.execute(TestCoins.BITCOIN_ID, forceRefresh = true)

        assertThat(result).isEqualTo(Result.Error(DataError.Remote.REQUEST_TIMEOUT))
    }
}
