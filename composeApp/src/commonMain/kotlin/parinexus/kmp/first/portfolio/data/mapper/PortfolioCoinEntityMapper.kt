package parinexus.kmp.first.portfolio.data.mapper

import kotlinx.datetime.Clock
import parinexus.kmp.first.core.domain.coin.Coin
import parinexus.kmp.first.portfolio.data.local.PortfolioCoinEntity
import parinexus.kmp.first.portfolio.domain.PortfolioCoinModel

fun PortfolioCoinEntity.toPortfolioCoinModel(
    currentPrice: Double
): PortfolioCoinModel {
    return PortfolioCoinModel(
        coin = Coin(
            id = coinId,
            name = name,
            iconUrl = iconUrl,
            symbol = symbol,
        ),
        performancePercent = (currentPrice - averagePurchasePrice) / averagePurchasePrice * 100,
        averagePurchasePrice = averagePurchasePrice,
        ownedAmountInUnit = amountOwned,
        ownedAmountInFiat = amountOwned * currentPrice,
    )
}


fun PortfolioCoinModel.toPortfolioCoinEntity(): PortfolioCoinEntity{
    return PortfolioCoinEntity(
        coinId = coin.id,
        name = coin.name,
        iconUrl = coin.iconUrl,
        symbol = coin.symbol,
        amountOwned = ownedAmountInUnit,
        averagePurchasePrice = averagePurchasePrice,
        timestamp = Clock.System.now().toEpochMilliseconds()
    )
}