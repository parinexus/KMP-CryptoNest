package parinexus.kmp.first.coins.domain

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.core.domain.cache.DataFreshness
import parinexus.kmp.first.test.fake.FakeCoinsRepository
import parinexus.kmp.first.test.fake.FakeCoinsRemoteDataSource
import parinexus.kmp.first.test.fixture.TestCoins

class FetchCoinDetailsUseCaseTest {

    private val remote = FakeCoinsRemoteDataSource()
    private val useCase = FetchCoinDetailsUseCase(FakeCoinsRepository(remote))

    @Test
    fun invoke_returnsCoinDetails() = runTest {
        useCase(TestCoins.BITCOIN_ID, forceRefresh = true).test {
            var item = awaitItem()
            while (item !is Result.Success || item.data.freshness != DataFreshness.Fresh) {
                item = awaitItem()
            }
            assertThat(item.data.value.coin.name).isEqualTo("Bitcoin")
            assertThat(item.data.value.price).isEqualTo(50_000.0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun invoke_propagatesRemoteError() = runTest {
        remote.setCoinDetailsResult(Result.Error(DataError.Remote.SERVER))
        val useCase = FetchCoinDetailsUseCase(FakeCoinsRepository(remote))

        useCase(TestCoins.BITCOIN_ID, forceRefresh = true).test {
            val item = awaitItem()
            assertThat(item is Result.Error).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
