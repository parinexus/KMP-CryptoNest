package parinexus.kmp.first.portfolio.data

import parinexus.kmp.first.portfolio.data.local.PortfolioCoinEntity

internal fun List<PortfolioCoinEntity>.sameHoldingsAs(other: List<PortfolioCoinEntity>): Boolean {
    if (size != other.size) return false
    return zip(other).all { (left, right) ->
        left.coinId == right.coinId &&
            left.amountOwned == right.amountOwned &&
            left.averagePurchasePrice == right.averagePurchasePrice
    }
}
