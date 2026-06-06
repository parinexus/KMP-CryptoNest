package parinexus.kmp.first.trade.domain

import kotlinx.coroutines.flow.first
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.EmptyResult
import parinexus.kmp.first.core.domain.coin.Coin
import parinexus.kmp.first.portfolio.domain.PortfolioRepository
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.trade.domain.model.TradeRecordDraft
import parinexus.kmp.first.trade.domain.model.TradeType

class SellCoinUseCase(
    private val portfolioRepository: PortfolioRepository,
    private val recordTradeUseCase: RecordTradeUseCase,
) {

    suspend fun sellCoin(
        coin: Coin,
        amountInFiat: Double,
        price: Double,
    ): EmptyResult<DataError> {
        val sellAllThreshold = 1
        when(val existingCoinResponse = portfolioRepository.getPortfolioCoinById(coin.id)) {
            is Result.Success -> {
                val existingCoin = existingCoinResponse.data
                val sellAmountInUnit = amountInFiat / price

                val balance = portfolioRepository.totalCashBalanceFlow().first()
                if (existingCoin == null || existingCoin.ownedAmountInUnit < sellAmountInUnit) {
                    return Result.Error(DataError.Local.INSUFFICIENT_FUNDS)
                }
                val remainingAmountFiat = existingCoin.ownedAmountInFiat - amountInFiat
                val remainingAmountUnit = existingCoin.ownedAmountInUnit - sellAmountInUnit
                if (remainingAmountFiat < sellAllThreshold) {
                    portfolioRepository.removePortfolioCoin(coin.id)
                } else {
                    when (
                        val updateResult = portfolioRepository.insertPortfolioCoin(
                            existingCoin.copy(
                                ownedAmountInUnit = remainingAmountUnit,
                                ownedAmountInFiat = remainingAmountFiat,
                            ),
                        )
                    ) {
                        is Result.Error -> return updateResult
                        is Result.Success -> Unit
                    }
                }
                portfolioRepository.updateCashBalance(balance + amountInFiat)
                return recordTradeUseCase.execute(
                    TradeRecordDraft(
                        coin = coin,
                        type = TradeType.SELL,
                        amountInFiat = amountInFiat,
                        amountInUnit = sellAmountInUnit,
                        priceAtTrade = price,
                    ),
                )
            }
            is Result.Error -> {
                return existingCoinResponse
            }
        }
    }
}