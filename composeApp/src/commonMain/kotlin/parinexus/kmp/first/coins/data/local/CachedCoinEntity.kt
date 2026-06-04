package parinexus.kmp.first.coins.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_coins")
data class CachedCoinEntity(
    @PrimaryKey val id: String,
    val name: String,
    val symbol: String,
    val iconUrl: String,
    val price: Double,
    val changePercent: Double,
    val rank: Int,
    val listOrder: Int,
)
