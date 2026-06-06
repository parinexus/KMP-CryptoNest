package parinexus.kmp.first.trade.data.repository

import androidx.sqlite.SQLiteException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.EmptyResult
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.trade.data.local.TradeHistoryDao
import parinexus.kmp.first.trade.data.mapper.toTradeRecord
import parinexus.kmp.first.trade.data.mapper.toTradeRecordEntity
import parinexus.kmp.first.trade.domain.model.TradeRecord
import parinexus.kmp.first.trade.domain.model.TradeRecordDraft
import parinexus.kmp.first.trade.domain.repository.TradeHistoryRepository

class TradeHistoryRepositoryImpl(
    private val tradeHistoryDao: TradeHistoryDao,
) : TradeHistoryRepository {

    override fun observeTradeHistory(): Flow<List<TradeRecord>> =
        tradeHistoryDao.observeAll().map { entities ->
            entities.map { it.toTradeRecord() }
        }

    override suspend fun recordTrade(draft: TradeRecordDraft): EmptyResult<DataError.Local> {
        return try {
            tradeHistoryDao.insert(draft.toTradeRecordEntity())
            Result.Success(Unit)
        } catch (_: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }
}
