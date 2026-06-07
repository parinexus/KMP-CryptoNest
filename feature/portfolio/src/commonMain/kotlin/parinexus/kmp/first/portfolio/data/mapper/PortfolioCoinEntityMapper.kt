package parinexus.kmp.first.portfolio.data.mapper

import kotlinx.datetime.Clock
import parinexus.kmp.first.core.domain.coin.Coin
import parinexus.kmp.first.portfolio.data.local.PortfolioCoinEntity
import parinexus.kmp.first.portfolio.domain.PortfolioCoinModel
import parinexus.kmp.first.portfolio.domain.PortfolioHolding

fun PortfolioCoinEntity.toPortfolioHolding(): PortfolioHolding = PortfolioHolding(
    coin = Coin(
        id = coinId,
        name = name,
        iconUrl = iconUrl,
        symbol = symbol,
    ),
    ownedAmountInUnit = amountOwned,
    averagePurchasePrice = averagePurchasePrice,
)

fun PortfolioHolding.toPortfolioCoinEntity(): PortfolioCoinEntity = PortfolioCoinEntity(
    coinId = coin.id,
    name = coin.name,
    iconUrl = coin.iconUrl,
    symbol = coin.symbol,
    amountOwned = ownedAmountInUnit,
    averagePurchasePrice = averagePurchasePrice,
    timestamp = Clock.System.now().toEpochMilliseconds(),
)

fun PortfolioCoinEntity.toPortfolioCoinModel(
    currentPrice: Double,
): PortfolioCoinModel = PortfolioCoinModel(
    coin = Coin(
        id = coinId,
        name = name,
        iconUrl = iconUrl,
        symbol = symbol,
    ),
    performancePercent = (currentPrice - averagePurchasePrice) / averagePurchasePrice * 100,
    averagePurchasePrice = averagePurchasePrice,
    ownedAmountInUnit = amountOwned,
    marketValueFiat = amountOwned * currentPrice,
)

private fun resolvePriceForEntity(
    entity: PortfolioCoinEntity,
    pricesByCoinId: Map<String, Double>,
): Double = pricesByCoinId[entity.coinId] ?: entity.averagePurchasePrice

fun List<PortfolioCoinEntity>.toPortfolioCoinModels(
    pricesByCoinId: Map<String, Double>,
): List<PortfolioCoinModel> = map { entity ->
    entity.toPortfolioCoinModel(resolvePriceForEntity(entity, pricesByCoinId))
}

fun List<PortfolioCoinEntity>.portfolioMarketValue(
    pricesByCoinId: Map<String, Double>,
): Double = sumOf { entity ->
    entity.amountOwned * resolvePriceForEntity(entity, pricesByCoinId)
}
