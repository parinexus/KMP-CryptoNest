package parinexus.kmp.first.test.fake

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.EmptyResult
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.portfolio.domain.PortfolioCoinModel
import parinexus.kmp.first.portfolio.domain.PortfolioRepository

class FakePortfolioRepository(
    cashBalance: Double = 10_000.0,
    ownedCoins: Map<String, PortfolioCoinModel> = emptyMap(),
    private var portfolioLookupResult: Result<PortfolioCoinModel?, DataError.Remote>? = null,
    private var insertResult: EmptyResult<DataError.Local> = Result.Success(Unit),
) : PortfolioRepository {

    private val cashBalanceFlow = MutableStateFlow(cashBalance)
    private val ownedCoinsState = ownedCoins.toMutableMap()

    val insertedCoins = mutableListOf<PortfolioCoinModel>()
    val removedCoinIds = mutableListOf<String>()
    val updatedCashBalances = mutableListOf<Double>()
    var initUserBalanceCalled = false
        private set

    fun setCashBalance(balance: Double) {
        cashBalanceFlow.value = balance
    }

    fun setOwnedCoin(coinId: String, model: PortfolioCoinModel?) {
        if (model == null) {
            ownedCoinsState.remove(coinId)
        } else {
            ownedCoinsState[coinId] = model
        }
    }

    fun setPortfolioLookupResult(result: Result<PortfolioCoinModel?, DataError.Remote>) {
        portfolioLookupResult = result
    }

    fun setInsertResult(result: EmptyResult<DataError.Local>) {
        insertResult = result
    }

    override suspend fun initUserBalance() {
        initUserBalanceCalled = true
    }

    override fun getOwnedCoins(): Flow<Result<List<PortfolioCoinModel>, DataError.Remote>> =
        flowOf(Result.Success(ownedCoinsState.values.toList()))

    override suspend fun getPortfolioCoinById(coinId: String): Result<PortfolioCoinModel?, DataError.Remote> {
        return portfolioLookupResult ?: Result.Success(ownedCoinsState[coinId])
    }

    override suspend fun insertPortfolioCoin(portfolioCoinModel: PortfolioCoinModel): EmptyResult<DataError.Local> {
        insertedCoins.add(portfolioCoinModel)
        ownedCoinsState[portfolioCoinModel.coin.id] = portfolioCoinModel
        return insertResult
    }

    override suspend fun removePortfolioCoin(coinId: String) {
        removedCoinIds.add(coinId)
        ownedCoinsState.remove(coinId)
    }

    override fun calculatePortfolioValue(): Flow<Result<Double, DataError.Remote>> =
        flowOf(Result.Success(ownedCoinsState.values.sumOf { it.ownedAmountInFiat }))

    override fun totalBalanceFlow(): Flow<Result<Double, DataError.Remote>> =
        flowOf(Result.Success(cashBalanceFlow.value + ownedCoinsState.values.sumOf { it.ownedAmountInFiat }))

    override fun totalCashBalanceFlow(): Flow<Double> = cashBalanceFlow

    override suspend fun updateCashBalance(newBalance: Double) {
        updatedCashBalances.add(newBalance)
        cashBalanceFlow.update { newBalance }
    }
}
