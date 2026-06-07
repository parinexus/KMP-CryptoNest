package parinexus.kmp.first.trade.domain

import kotlinx.coroutines.flow.Flow
import parinexus.kmp.first.trade.domain.model.TradeRecord
import parinexus.kmp.first.trade.domain.repository.TradeHistoryRepository

class ObserveTradeHistoryUseCase(
    private val tradeHistoryRepository: TradeHistoryRepository,
) {

    operator fun invoke(): Flow<List<TradeRecord>> =
        tradeHistoryRepository.observeTradeHistory()
}
