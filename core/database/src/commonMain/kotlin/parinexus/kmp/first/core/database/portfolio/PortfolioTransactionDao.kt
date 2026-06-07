package parinexus.kmp.first.core.database.portfolio

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import parinexus.kmp.first.core.domain.InsufficientFundsException
import parinexus.kmp.first.portfolio.data.local.PortfolioCoinEntity
import parinexus.kmp.first.trade.data.local.TradeRecordEntity

@Dao
abstract class PortfolioTransactionDao {

    @Upsert
    protected abstract suspend fun upsertPortfolio(entity: PortfolioCoinEntity)

    @Query("DELETE FROM PortfolioCoinEntity WHERE coinId = :coinId")
    protected abstract suspend fun deletePortfolio(coinId: String)

    @Query(
        """
        UPDATE UserBalanceEntity
        SET cashBalance = cashBalance - :amount
        WHERE id = 1 AND cashBalance >= :amount
        """,
    )
    protected abstract suspend fun deductCash(amount: Double): Int

    @Query(
        """
        UPDATE UserBalanceEntity
        SET cashBalance = cashBalance + :amount
        WHERE id = 1
        """,
    )
    protected abstract suspend fun addCash(amount: Double): Int

    @Insert
    protected abstract suspend fun insertTrade(entity: TradeRecordEntity)

    @Transaction
    open suspend fun executeBuyWithTrade(
        portfolioUpdate: PortfolioCoinEntity,
        amountInFiat: Double,
        tradeRecord: TradeRecordEntity,
    ) {
        if (deductCash(amountInFiat) == 0) {
            throw InsufficientFundsException()
        }
        upsertPortfolio(portfolioUpdate)
        insertTrade(tradeRecord)
    }

    @Transaction
    open suspend fun executeSellWithTrade(
        portfolioUpdate: PortfolioCoinEntity?,
        removeCoinId: String?,
        amountInFiat: Double,
        tradeRecord: TradeRecordEntity,
    ) {
        when {
            removeCoinId != null -> deletePortfolio(removeCoinId)
            portfolioUpdate != null -> upsertPortfolio(portfolioUpdate)
        }
        addCash(amountInFiat)
        insertTrade(tradeRecord)
    }
}
