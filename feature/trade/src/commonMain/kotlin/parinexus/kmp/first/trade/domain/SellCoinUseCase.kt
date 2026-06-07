package parinexus.kmp.first.trade.domain

import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.EmptyResult
import parinexus.kmp.first.core.domain.coin.Coin
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.portfolio.domain.PortfolioHolding
import parinexus.kmp.first.portfolio.domain.PortfolioRepository
import parinexus.kmp.first.trade.domain.model.TradeRecordDraft
import parinexus.kmp.first.trade.domain.model.TradeType

class SellCoinUseCase(
    private val portfolioRepository: PortfolioRepository,
    private val tradePortfolioWriter: TradePortfolioWriter,
) {

    suspend fun sellCoin(
        coin: Coin,
        amountInFiat: Double,
        price: Double,
    ): EmptyResult<DataError> {
        val existingHolding = portfolioRepository.getPortfolioHolding(coin.id)
            ?: return Result.Error(DataError.Local.INSUFFICIENT_FUNDS)

        val sellAmountInUnit = amountInFiat / price
        if (existingHolding.ownedAmountInUnit < sellAmountInUnit) {
            return Result.Error(DataError.Local.INSUFFICIENT_FUNDS)
        }

        val remainingAmountUnit = existingHolding.ownedAmountInUnit - sellAmountInUnit
        val removeCoinId = if (remainingAmountUnit <= DUST_THRESHOLD_UNITS) coin.id else null
        val holdingUpdate = if (removeCoinId == null) {
            existingHolding.copy(ownedAmountInUnit = remainingAmountUnit)
        } else {
            null
        }

        return tradePortfolioWriter.executeSell(
            holdingUpdate = holdingUpdate,
            removeCoinId = removeCoinId,
            amountInFiat = amountInFiat,
            draft = TradeRecordDraft(
                coin = coin,
                type = TradeType.SELL,
                amountInFiat = amountInFiat,
                amountInUnit = sellAmountInUnit,
                priceAtTrade = price,
            ),
        )
    }

    private companion object {
        const val DUST_THRESHOLD_UNITS = 1e-8
    }
}
