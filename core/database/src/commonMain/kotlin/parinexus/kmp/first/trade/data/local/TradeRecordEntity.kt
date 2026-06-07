package parinexus.kmp.first.trade.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trade_records")
data class TradeRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val coinIconUrl: String,
    val type: String,
    val amountInFiat: Double,
    val amountInUnit: Double,
    val priceAtTrade: Double,
    val executedAtEpochMs: Long,
)
