package parinexus.kmp.first.portfolio.domain

/** Single read model for the portfolio screen — one price resolution per emission. */
data class PortfolioSnapshot(
    val coins: List<PortfolioCoinModel>,
    val cashBalance: Double,
    val portfolioMarketValue: Double,
    val totalBalance: Double,
)
