package parinexus.kmp.first.trade.domain

import kotlinx.coroutines.flow.first
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.EmptyResult
import parinexus.kmp.first.core.domain.coin.Coin
import parinexus.kmp.first.portfolio.domain.PortfolioRepository
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.portfolio.domain.PortfolioCoinModel
import parinexus.kmp.first.trade.domain.model.TradeRecordDraft
import parinexus.kmp.first.trade.domain.model.TradeType

class BuyCoinUseCase(
    private val portfolioRepository: PortfolioRepository,
    private val recordTradeUseCase: RecordTradeUseCase,
) {

    suspend fun buyCoin(
        coin: Coin,
        amountInFiat: Double,
        price: Double,
    ): EmptyResult<DataError> {
        val balance = portfolioRepository.totalCashBalanceFlow().first()
        if (balance < amountInFiat) {
            return Result.Error(DataError.Local.INSUFFICIENT_FUNDS)
        }

        val existingCoinResult = portfolioRepository.getPortfolioCoinById(coin.id)
        val existingCoin = when (existingCoinResult) {
            is Result.Success -> existingCoinResult.data
            is Result.Error -> return Result.Error(existingCoinResult.error)
        }
        val amountInUnit = amountInFiat / price
        val insertResult = if (existingCoin != null) {
            val newAmountOwned = existingCoin.ownedAmountInUnit + amountInUnit
            val newTotalInvestment = existingCoin.ownedAmountInFiat + amountInFiat
            val newAveragePurchasePrice = newTotalInvestment / newAmountOwned
            portfolioRepository.insertPortfolioCoin(
                existingCoin.copy(
                    ownedAmountInUnit = newAmountOwned,
                    ownedAmountInFiat = newTotalInvestment,
                    averagePurchasePrice = newAveragePurchasePrice,
                ),
            )
        } else {
            portfolioRepository.insertPortfolioCoin(
                PortfolioCoinModel(
                    coin = coin,
                    performancePercent = 0.0,
                    averagePurchasePrice = price,
                    ownedAmountInFiat = amountInFiat,
                    ownedAmountInUnit = amountInUnit,
                ),
            )
        }
        if (insertResult is Result.Error) {
            return insertResult
        }
        portfolioRepository.updateCashBalance(balance - amountInFiat)
        return recordTradeUseCase.execute(
            TradeRecordDraft(
                coin = coin,
                type = TradeType.BUY,
                amountInFiat = amountInFiat,
                amountInUnit = amountInUnit,
                priceAtTrade = price,
            ),
        )
    }
}