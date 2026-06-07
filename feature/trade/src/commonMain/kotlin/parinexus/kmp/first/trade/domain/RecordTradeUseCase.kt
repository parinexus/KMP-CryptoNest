package parinexus.kmp.first.trade.domain

import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.EmptyResult
import parinexus.kmp.first.trade.domain.model.TradeRecordDraft
import parinexus.kmp.first.trade.domain.repository.TradeHistoryRepository

/** Standalone trade-history writes (e.g. tests). Buy/sell flows use [TradePortfolioWriter] atomically. */
class RecordTradeUseCase(
    private val tradeHistoryRepository: TradeHistoryRepository,
) {

    suspend fun execute(draft: TradeRecordDraft): EmptyResult<DataError.Local> =
        tradeHistoryRepository.recordTrade(draft)
}
