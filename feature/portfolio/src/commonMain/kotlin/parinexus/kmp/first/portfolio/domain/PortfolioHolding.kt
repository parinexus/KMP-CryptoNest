package parinexus.kmp.first.portfolio.domain

import parinexus.kmp.first.core.domain.coin.Coin

/** Persisted portfolio position — no live market price. Used for trade calculations and writes. */
data class PortfolioHolding(
    val coin: Coin,
    val ownedAmountInUnit: Double,
    val averagePurchasePrice: Double,
) {
    val costBasisFiat: Double
        get() = ownedAmountInUnit * averagePurchasePrice
}
