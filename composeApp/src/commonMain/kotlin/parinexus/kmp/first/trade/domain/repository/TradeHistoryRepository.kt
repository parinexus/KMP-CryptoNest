package parinexus.kmp.first.trade.domain.repository

import kotlinx.coroutines.flow.Flow
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.EmptyResult
import parinexus.kmp.first.trade.domain.model.TradeRecord
import parinexus.kmp.first.trade.domain.model.TradeRecordDraft

interface TradeHistoryRepository {

    fun observeTradeHistory(): Flow<List<TradeRecord>>

    suspend fun recordTrade(draft: TradeRecordDraft): EmptyResult<DataError.Local>
}
