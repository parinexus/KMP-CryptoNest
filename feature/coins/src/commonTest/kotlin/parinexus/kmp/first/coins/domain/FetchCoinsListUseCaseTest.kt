package parinexus.kmp.first.coins.domain

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.core.domain.cache.DataFreshness
import parinexus.kmp.first.test.fake.FakeCoinsRepository
import parinexus.kmp.first.test.fake.FakeCoinsRemoteDataSource
import parinexus.kmp.first.test.fixture.TestCoins

class FetchCoinsListUseCaseTest {

    private val remote = FakeCoinsRemoteDataSource()
    private val useCase = FetchCoinsListUseCase(FakeCoinsRepository(remote))

    @Test
    fun invoke_returnsMappedCoinsOnSuccess() = runTest {
        useCase().test {
            var item = awaitItem()
            while (item !is Result.Success || item.data.freshness != DataFreshness.Fresh) {
                item = awaitItem()
            }
            assertThat(item.data.value).hasSize(2)
            assertThat(item.data.value.first().coin.id).isEqualTo(TestCoins.BITCOIN_ID)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun invoke_propagatesRemoteErrorWhenNoCache() = runTest {
        remote.setCoinsResult(
            parinexus.kmp.first.core.domain.Result.Error(
                parinexus.kmp.first.core.domain.DataError.Remote.NO_INTERNET,
            ),
        )
        val useCaseNoCache = FetchCoinsListUseCase(FakeCoinsRepository(remote))

        useCaseNoCache(forceRefresh = true).test {
            val item = awaitItem()
            assertThat(item is Result.Error).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
