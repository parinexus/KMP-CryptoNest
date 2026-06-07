package parinexus.kmp.first.test.fake

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.EmptyResult
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.trade.domain.model.TradeRecord
import parinexus.kmp.first.trade.domain.model.TradeRecordDraft
import parinexus.kmp.first.trade.domain.repository.TradeHistoryRepository

class FakeTradeHistoryRepository(
    initialTrades: List<TradeRecord> = emptyList(),
    private var recordResult: EmptyResult<DataError.Local> = Result.Success(Unit),
) : TradeHistoryRepository {

    private val tradesState = MutableStateFlow(initialTrades)
    val recordedDrafts = mutableListOf<TradeRecordDraft>()

    fun setRecordResult(result: EmptyResult<DataError.Local>) {
        recordResult = result
    }

    override fun observeTradeHistory(): Flow<List<TradeRecord>> =
        tradesState.asStateFlow()

    override suspend fun recordTrade(draft: TradeRecordDraft): EmptyResult<DataError.Local> {
        recordedDrafts.add(draft)
        if (recordResult is Result.Error) {
            return recordResult
        }
        val nextId = (tradesState.value.maxOfOrNull { it.id } ?: 0L) + 1L
        tradesState.update { current ->
            current + TradeRecord(
                id = nextId,
                coin = draft.coin,
                type = draft.type,
                amountInFiat = draft.amountInFiat,
                amountInUnit = draft.amountInUnit,
                priceAtTrade = draft.priceAtTrade,
                executedAtEpochMs = 1_700_000_000_000L + nextId,
            )
        }
        return Result.Success(Unit)
    }
}
