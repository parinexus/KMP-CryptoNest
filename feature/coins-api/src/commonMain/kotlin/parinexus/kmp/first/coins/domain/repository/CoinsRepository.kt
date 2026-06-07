package parinexus.kmp.first.coins.domain.repository

import kotlinx.coroutines.flow.Flow
import parinexus.kmp.first.coins.domain.model.CoinDetailModel
import parinexus.kmp.first.coins.domain.model.CoinInfoModel
import parinexus.kmp.first.coins.domain.model.PriceModel
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.core.domain.cache.CachedData

/**
 * Single source of truth for market data — coordinates remote API and Room cache.
 */
interface CoinsRepository {

    fun observeCoinsList(forceRefresh: Boolean = false): Flow<Result<CachedData<List<CoinInfoModel>>, DataError.Remote>>

    fun observeCoinDetail(
        coinId: String,
        forceRefresh: Boolean = false,
    ): Flow<Result<CachedData<CoinDetailModel>, DataError.Remote>>

    suspend fun getPriceHistory(
        coinId: String,
        forceRefresh: Boolean = false,
    ): Result<CachedData<List<PriceModel>>, DataError.Remote>

    /** Local cache snapshot only — no network I/O. */
    suspend fun getCachedPricesByCoinId(): Map<String, Double>

    /**
     * Resolves USD prices for the given coin ids using a cache-first strategy.
     * Network is used only when cache is missing, stale, or incomplete for the requested ids.
     */
    suspend fun resolveMarketPrices(
        coinIds: Collection<String>,
        forceRefresh: Boolean = false,
    ): Result<Map<String, Double>, DataError.Remote>
}
