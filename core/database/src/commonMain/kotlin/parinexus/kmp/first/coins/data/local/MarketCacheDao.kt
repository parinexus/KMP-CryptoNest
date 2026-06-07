package parinexus.kmp.first.coins.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface MarketCacheDao {

    @Query("SELECT * FROM cached_coins ORDER BY listOrder ASC")
    suspend fun getCachedCoins(): List<CachedCoinEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoins(coins: List<CachedCoinEntity>)

    @Query("DELETE FROM cached_coins")
    suspend fun clearCoins()

    @Query("SELECT * FROM market_cache_meta WHERE cacheKey = :cacheKey LIMIT 1")
    suspend fun getMeta(cacheKey: String): MarketCacheMetaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMeta(meta: MarketCacheMetaEntity)

    @Query("SELECT * FROM cached_coin_details WHERE coinId = :coinId LIMIT 1")
    suspend fun getCoinDetail(coinId: String): CachedCoinDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCoinDetail(entity: CachedCoinDetailEntity)

    @Query("SELECT * FROM cached_price_history WHERE coinId = :coinId LIMIT 1")
    suspend fun getPriceHistory(coinId: String): CachedPriceHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPriceHistory(entity: CachedPriceHistoryEntity)

    @Transaction
    suspend fun replaceCoinsList(coins: List<CachedCoinEntity>, meta: MarketCacheMetaEntity) {
        clearCoins()
        insertCoins(coins)
        upsertMeta(meta)
    }
}
