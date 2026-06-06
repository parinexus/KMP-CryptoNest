package parinexus.kmp.first.trade.domain

import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.EmptyResult
import parinexus.kmp.first.trade.domain.model.TradeRecordDraft
import parinexus.kmp.first.trade.domain.repository.TradeHistoryRepository

class RecordTradeUseCase(
    private val tradeHistoryRepository: TradeHistoryRepository,
) {

    suspend fun execute(draft: TradeRecordDraft): EmptyResult<DataError.Local> =
        tradeHistoryRepository.recordTrade(draft)
}
