package parinexus.kmp.first.test.fake

import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.EmptyResult
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.portfolio.domain.PortfolioHolding
import parinexus.kmp.first.trade.domain.RecordTradeUseCase
import parinexus.kmp.first.trade.domain.TradePortfolioWriter
import parinexus.kmp.first.trade.domain.model.TradeRecordDraft

class FakeTradePortfolioWriter(
    private val portfolio: FakePortfolioRepository,
    private val tradeHistory: FakeTradeHistoryRepository,
    private var executeResult: EmptyResult<DataError.Local> = Result.Success(Unit),
) : TradePortfolioWriter {

    val buyCalls = mutableListOf<Pair<PortfolioHolding, TradeRecordDraft>>()
    val sellCalls = mutableListOf<SellCall>()

    data class SellCall(
        val holdingUpdate: PortfolioHolding?,
        val removeCoinId: String?,
        val amountInFiat: Double,
        val draft: TradeRecordDraft,
    )

    fun setExecuteResult(result: EmptyResult<DataError.Local>) {
        executeResult = result
    }

    override suspend fun executeBuy(
        holding: PortfolioHolding,
        amountInFiat: Double,
        draft: TradeRecordDraft,
    ): EmptyResult<DataError.Local> {
        buyCalls.add(holding to draft)
        if (executeResult is Result.Error) return executeResult
        portfolio.applyBuy(holding, amountInFiat)
        return RecordTradeUseCase(tradeHistory).execute(draft)
    }

    override suspend fun executeSell(
        holdingUpdate: PortfolioHolding?,
        removeCoinId: String?,
        amountInFiat: Double,
        draft: TradeRecordDraft,
    ): EmptyResult<DataError.Local> {
        sellCalls.add(SellCall(holdingUpdate, removeCoinId, amountInFiat, draft))
        if (executeResult is Result.Error) return executeResult
        portfolio.applySell(holdingUpdate, removeCoinId, amountInFiat)
        return RecordTradeUseCase(tradeHistory).execute(draft)
    }
}
