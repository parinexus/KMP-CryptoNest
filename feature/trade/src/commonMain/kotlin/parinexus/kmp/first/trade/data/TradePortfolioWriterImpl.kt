package parinexus.kmp.first.trade.data

import androidx.sqlite.SQLiteException
import kotlinx.datetime.Clock
import parinexus.kmp.first.core.database.portfolio.PortfolioTransactionDao
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.EmptyResult
import parinexus.kmp.first.core.domain.InsufficientFundsException
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.portfolio.data.local.PortfolioCoinEntity
import parinexus.kmp.first.portfolio.domain.PortfolioHolding
import parinexus.kmp.first.trade.data.mapper.toTradeRecordEntity
import parinexus.kmp.first.trade.domain.TradePortfolioWriter
import parinexus.kmp.first.trade.domain.model.TradeRecordDraft

class TradePortfolioWriterImpl(
    private val transactionDao: PortfolioTransactionDao,
) : TradePortfolioWriter {

    override suspend fun executeBuy(
        holding: PortfolioHolding,
        amountInFiat: Double,
        draft: TradeRecordDraft,
    ): EmptyResult<DataError.Local> = runTransaction {
        transactionDao.executeBuyWithTrade(
            portfolioUpdate = holding.toPortfolioCoinEntity(),
            amountInFiat = amountInFiat,
            tradeRecord = draft.toTradeRecordEntity(),
        )
    }

    override suspend fun executeSell(
        holdingUpdate: PortfolioHolding?,
        removeCoinId: String?,
        amountInFiat: Double,
        draft: TradeRecordDraft,
    ): EmptyResult<DataError.Local> = runTransaction {
        transactionDao.executeSellWithTrade(
            portfolioUpdate = holdingUpdate?.toPortfolioCoinEntity(),
            removeCoinId = removeCoinId,
            amountInFiat = amountInFiat,
            tradeRecord = draft.toTradeRecordEntity(),
        )
    }

    private inline fun runTransaction(block: () -> Unit): EmptyResult<DataError.Local> =
        try {
            block()
            Result.Success(Unit)
        } catch (_: InsufficientFundsException) {
            Result.Error(DataError.Local.INSUFFICIENT_FUNDS)
        } catch (_: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)
        }

    private fun PortfolioHolding.toPortfolioCoinEntity(): PortfolioCoinEntity =
        PortfolioCoinEntity(
            coinId = coin.id,
            name = coin.name,
            iconUrl = coin.iconUrl,
            symbol = coin.symbol,
            amountOwned = ownedAmountInUnit,
            averagePurchasePrice = averagePurchasePrice,
            timestamp = Clock.System.now().toEpochMilliseconds(),
        )
}
