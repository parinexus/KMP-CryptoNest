package parinexus.kmp.first.trade.domain

import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.EmptyResult
import parinexus.kmp.first.portfolio.domain.PortfolioHolding
import parinexus.kmp.first.trade.domain.model.TradeRecordDraft

interface TradePortfolioWriter {

    suspend fun executeBuy(
        holding: PortfolioHolding,
        amountInFiat: Double,
        draft: TradeRecordDraft,
    ): EmptyResult<DataError.Local>

    suspend fun executeSell(
        holdingUpdate: PortfolioHolding?,
        removeCoinId: String?,
        amountInFiat: Double,
        draft: TradeRecordDraft,
    ): EmptyResult<DataError.Local>
}
