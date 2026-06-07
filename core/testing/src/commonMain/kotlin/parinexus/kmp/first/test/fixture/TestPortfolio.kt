package parinexus.kmp.first.test.fixture

import parinexus.kmp.first.portfolio.domain.PortfolioCoinModel
import parinexus.kmp.first.portfolio.domain.PortfolioHolding

object TestPortfolio {
    fun bitcoinHolding(
        ownedAmountInUnit: Double = 0.5,
        marketValueFiat: Double = 25_000.0,
        averagePurchasePrice: Double = 48_000.0,
        performancePercent: Double = 4.0,
    ) = PortfolioCoinModel(
        coin = TestCoins.bitcoin,
        performancePercent = performancePercent,
        averagePurchasePrice = averagePurchasePrice,
        ownedAmountInUnit = ownedAmountInUnit,
        marketValueFiat = marketValueFiat,
    )

    fun bitcoinPortfolioHolding(
        ownedAmountInUnit: Double = 0.5,
        averagePurchasePrice: Double = 48_000.0,
    ) = PortfolioHolding(
        coin = TestCoins.bitcoin,
        ownedAmountInUnit = ownedAmountInUnit,
        averagePurchasePrice = averagePurchasePrice,
    )
}
