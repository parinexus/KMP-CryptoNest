package parinexus.kmp.first

import parinexus.kmp.first.portfolio.domain.PortfolioRepository

/** One-shot app bootstrap — keeps side effects out of ViewModels. */
class AppInitializer(
    private val portfolioRepository: PortfolioRepository,
) {
    suspend fun initialize() {
        portfolioRepository.initUserBalance()
    }
}
