package parinexus.kmp.first.trade.domain

import kotlinx.coroutines.flow.first
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.EmptyResult
import parinexus.kmp.first.core.domain.coin.Coin
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.portfolio.domain.PortfolioHolding
import parinexus.kmp.first.portfolio.domain.PortfolioRepository
import parinexus.kmp.first.trade.domain.model.TradeRecordDraft
import parinexus.kmp.first.trade.domain.model.TradeType

class BuyCoinUseCase(
    private val portfolioRepository: PortfolioRepository,
    private val tradePortfolioWriter: TradePortfolioWriter,
) {

    suspend fun buyCoin(
        coin: Coin,
        amountInFiat: Double,
        price: Double,
    ): EmptyResult<DataError> {
        val balance = portfolioRepository.observeCashBalance().first()
        if (balance < amountInFiat) {
            return Result.Error(DataError.Local.INSUFFICIENT_FUNDS)
        }

        val amountInUnit = amountInFiat / price
        val existingHolding = portfolioRepository.getPortfolioHolding(coin.id)
        val updatedHolding = if (existingHolding != null) {
            val newAmountOwned = existingHolding.ownedAmountInUnit + amountInUnit
            val newCostBasis = existingHolding.costBasisFiat + amountInFiat
            existingHolding.copy(
                ownedAmountInUnit = newAmountOwned,
                averagePurchasePrice = newCostBasis / newAmountOwned,
            )
        } else {
            PortfolioHolding(
                coin = coin,
                ownedAmountInUnit = amountInUnit,
                averagePurchasePrice = price,
            )
        }

        return tradePortfolioWriter.executeBuy(
            holding = updatedHolding,
            amountInFiat = amountInFiat,
            draft = TradeRecordDraft(
                coin = coin,
                type = TradeType.BUY,
                amountInFiat = amountInFiat,
                amountInUnit = amountInUnit,
                priceAtTrade = price,
            ),
        )
    }
}
