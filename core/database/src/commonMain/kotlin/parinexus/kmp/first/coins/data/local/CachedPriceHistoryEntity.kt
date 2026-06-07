package parinexus.kmp.first.coins.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_price_history")
data class CachedPriceHistoryEntity(
    @PrimaryKey val coinId: String,
    val responseJson: String,
    val cachedAtEpochMs: Long,
)
