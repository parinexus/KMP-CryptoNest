package parinexus.kmp.first.coins.data.repository

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import parinexus.kmp.first.coins.data.mapper.toCoinInfoModel
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.core.domain.cache.DataFreshness
import parinexus.kmp.first.test.fake.FakeCoinsRemoteDataSource
import parinexus.kmp.first.test.fake.FakeCoinsRepository
import parinexus.kmp.first.test.fake.FakeMarketLocalDataSource
import parinexus.kmp.first.test.fixture.TestCoins

class CoinsRepositoryImplTest {

    @Test
    fun observeCoinsList_emitsCacheThenFresh() = runTest {
        val remote = FakeCoinsRemoteDataSource()
        val local = FakeMarketLocalDataSource()
        local.coins = listOf(TestCoins.bitcoinDto.toCoinInfoModel())
        local.coinsCachedAt = 0L
        val repository = FakeCoinsRepository(remote, local)

        repository.observeCoinsList(forceRefresh = false).test {
            val cached = awaitItem()
            assertThat(cached).isInstanceOf(Result.Success::class)
            assertThat((cached as Result.Success).data.freshness).isEqualTo(DataFreshness.Stale)

            val fresh = awaitItem()
            assertThat((fresh as Result.Success).data.freshness).isEqualTo(DataFreshness.Fresh)
            assertThat(fresh.data.value).hasSize(2)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeCoinsList_servesOfflineCacheWhenRemoteFails() = runTest {
        val remote = FakeCoinsRemoteDataSource(
            coinsResult = Result.Error(DataError.Remote.NO_INTERNET),
        )
        val local = FakeMarketLocalDataSource()
        local.coins = listOf(TestCoins.bitcoinDto.toCoinInfoModel())
        local.coinsCachedAt = 1L
        val repository = FakeCoinsRepository(remote, local)

        repository.observeCoinsList(forceRefresh = true).test {
            val result = awaitItem()
            assertThat(result).isInstanceOf(Result.Success::class)
            val success = result as Result.Success
            assertThat(success.data.freshness).isEqualTo(DataFreshness.Offline)
            assertThat(success.data.value).hasSize(1)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeCoinsList_withoutCacheAndRemoteError_returnsError() = runTest {
        val remote = FakeCoinsRemoteDataSource(
            coinsResult = Result.Error(DataError.Remote.NO_INTERNET),
        )
        val repository = FakeCoinsRepository(remote)

        repository.observeCoinsList(forceRefresh = true).test {
            val result = awaitItem()
            assertThat(result).isInstanceOf(Result.Error::class)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
