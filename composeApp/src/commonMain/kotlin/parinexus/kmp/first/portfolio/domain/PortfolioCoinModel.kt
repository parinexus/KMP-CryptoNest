package parinexus.kmp.first.portfolio.domain

import parinexus.kmp.first.core.domain.coin.Coin

class PortfolioCoinModel(
    val coin: Coin,
    val performancePercent: Double,
    val averagePurchasePrice: Double,
    val ownedAmountInUnit: Double,
    val ownedAmountInFiat: Double,
)