package parinexus.kmp.first.trade.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeHistoryDao {

    @Query("SELECT * FROM trade_records ORDER BY executedAtEpochMs DESC")
    fun observeAll(): Flow<List<TradeRecordEntity>>

    @Insert
    suspend fun insert(entity: TradeRecordEntity): Long
}
