package parinexus.kmp.first.portfolio.domain

import kotlinx.coroutines.flow.Flow
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result

interface PortfolioRepository {

    suspend fun initUserBalance()

    /** Reactive portfolio screen state with a single market-price resolution per DB/cash update. */
    fun observePortfolioSnapshot(): Flow<Result<PortfolioSnapshot, DataError.Remote>>

    /** Local holding lookup for trade flows — no network I/O. */
    suspend fun getPortfolioHolding(coinId: String): PortfolioHolding?

    fun observeCashBalance(): Flow<Double>
}
