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

    /** Latest cached coins for portfolio pricing when the live list request fails. */
    suspend fun getCachedCoinsListOrNull(): List<CoinInfoModel>?
}
