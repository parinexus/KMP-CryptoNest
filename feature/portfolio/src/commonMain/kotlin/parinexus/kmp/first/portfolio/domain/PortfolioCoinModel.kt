package parinexus.kmp.first.portfolio.domain

import parinexus.kmp.first.core.domain.coin.Coin

/** Portfolio coin enriched with live market data for presentation. */
data class PortfolioCoinModel(
    val coin: Coin,
    val performancePercent: Double,
    val averagePurchasePrice: Double,
    val ownedAmountInUnit: Double,
    val marketValueFiat: Double,
)
