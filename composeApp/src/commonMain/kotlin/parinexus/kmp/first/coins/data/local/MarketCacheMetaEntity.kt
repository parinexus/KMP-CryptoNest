package parinexus.kmp.first.coins.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "market_cache_meta")
data class MarketCacheMetaEntity(
    @PrimaryKey val cacheKey: String,
    val cachedAtEpochMs: Long,
)
