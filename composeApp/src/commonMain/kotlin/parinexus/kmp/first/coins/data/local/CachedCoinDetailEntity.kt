package parinexus.kmp.first.coins.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_coin_details")
data class CachedCoinDetailEntity(
    @PrimaryKey val coinId: String,
    val responseJson: String,
    val cachedAtEpochMs: Long,
)
