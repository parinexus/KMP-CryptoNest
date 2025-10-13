package parinexus.kmp.first.portfolio.domain

import kotlinx.coroutines.flow.Flow
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.EmptyResult
import parinexus.kmp.first.core.domain.Result

interface PortfolioRepository {

    suspend fun initUserBalance()
    fun getOwnedCoins(): Flow<Result<List<PortfolioCoinModel>, DataError.Remote>>
    suspend fun getPortfolioCoinById(coinId: String): Result<PortfolioCoinModel?, DataError.Remote>
    suspend fun insertPortfolioCoin(portfolioCoinModel: PortfolioCoinModel): EmptyResult<DataError.Local>
    suspend fun removePortfolioCoin(coinId: String)

    fun calculatePortfolioValue(): Flow<Result<Double, DataError.Remote>>
    fun totalBalanceFlow(): Flow<Result<Double, DataError.Remote>>
    fun totalCashBalanceFlow(): Flow<Double>
    suspend fun updateCashBalance(newBalance: Double)
}