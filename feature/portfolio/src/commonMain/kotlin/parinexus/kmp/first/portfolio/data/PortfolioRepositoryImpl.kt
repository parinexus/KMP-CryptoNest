package parinexus.kmp.first.portfolio.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import parinexus.kmp.first.coins.domain.repository.CoinsRepository
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.portfolio.data.local.PortfolioCoinEntity
import parinexus.kmp.first.portfolio.data.local.PortfolioDao
import parinexus.kmp.first.portfolio.data.local.UserBalanceDao
import parinexus.kmp.first.portfolio.data.local.UserBalanceEntity
import parinexus.kmp.first.portfolio.data.mapper.portfolioMarketValue
import parinexus.kmp.first.portfolio.data.mapper.toPortfolioCoinModels
import parinexus.kmp.first.portfolio.data.mapper.toPortfolioHolding
import parinexus.kmp.first.portfolio.domain.PortfolioCoinModel
import parinexus.kmp.first.portfolio.domain.PortfolioHolding
import parinexus.kmp.first.portfolio.domain.PortfolioRepository
import parinexus.kmp.first.portfolio.domain.PortfolioSnapshot

class PortfolioRepositoryImpl(
    private val portfolioDao: PortfolioDao,
    private val userBalanceDao: UserBalanceDao,
    private val coinsRepository: CoinsRepository,
) : PortfolioRepository {

    override suspend fun initUserBalance() {
        if (userBalanceDao.getCashBalance() == null) {
            userBalanceDao.insertBalance(UserBalanceEntity(cashBalance = DEFAULT_CASH_BALANCE))
        }
    }

    override fun observePortfolioSnapshot(): Flow<Result<PortfolioSnapshot, DataError.Remote>> =
        combine(
            observePricedHoldings(),
            observeCashBalance(),
        ) { pricedResult, cashBalance ->
            when (pricedResult) {
                is Result.Success -> Result.Success(
                    PortfolioSnapshot(
                        coins = pricedResult.data.coins,
                        cashBalance = cashBalance,
                        portfolioMarketValue = pricedResult.data.portfolioMarketValue,
                        totalBalance = cashBalance + pricedResult.data.portfolioMarketValue,
                    ),
                )
                is Result.Error -> Result.Error(pricedResult.error)
            }
        }

    override suspend fun getPortfolioHolding(coinId: String): PortfolioHolding? =
        portfolioDao.getCoinById(coinId)?.toPortfolioHolding()

    override fun observeCashBalance(): Flow<Double> =
        userBalanceDao.observeCashBalance().map { balance ->
            balance ?: DEFAULT_CASH_BALANCE
        }

    private fun observePricedHoldings(): Flow<Result<PricedHoldings, DataError.Remote>> =
        portfolioDao.getAllOwnedCoins()
            .distinctUntilChanged { old, new -> old.sameHoldingsAs(new) }
            .map { entities -> entities.toPricedHoldingsResult(coinsRepository) }
            .catch { emit(Result.Error(DataError.Remote.UNKNOWN)) }

    private companion object {
        const val DEFAULT_CASH_BALANCE = 10_000.0
    }
}

private data class PricedHoldings(
    val coins: List<PortfolioCoinModel>,
    val portfolioMarketValue: Double,
)

private suspend fun List<PortfolioCoinEntity>.toPricedHoldingsResult(
    coinsRepository: CoinsRepository,
): Result<PricedHoldings, DataError.Remote> {
    if (isEmpty()) {
        return Result.Success(PricedHoldings(coins = emptyList(), portfolioMarketValue = 0.0))
    }
    return when (val pricesResult = coinsRepository.resolveMarketPrices(map { it.coinId })) {
        is Result.Success -> Result.Success(
            PricedHoldings(
                coins = toPortfolioCoinModels(pricesResult.data),
                portfolioMarketValue = portfolioMarketValue(pricesResult.data),
            ),
        )
        is Result.Error -> Result.Error(pricesResult.error)
    }
}
