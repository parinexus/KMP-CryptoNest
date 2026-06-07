package parinexus.kmp.first.test.fake

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.portfolio.domain.PortfolioCoinModel
import parinexus.kmp.first.portfolio.domain.PortfolioHolding
import parinexus.kmp.first.portfolio.domain.PortfolioRepository
import parinexus.kmp.first.portfolio.domain.PortfolioSnapshot

class FakePortfolioRepository(
    cashBalance: Double = 10_000.0,
    holdings: Map<String, PortfolioHolding> = emptyMap(),
    private var snapshotResult: Result<PortfolioSnapshot, DataError.Remote>? = null,
) : PortfolioRepository {

    private val cashBalanceFlow = MutableStateFlow(cashBalance)
    private val holdingsState = holdings.toMutableMap()
    private val holdingsRevision = MutableStateFlow(0)

    val appliedBuyHoldings = mutableListOf<PortfolioHolding>()
    val appliedSellHoldings = mutableListOf<PortfolioHolding?>()
    val removedCoinIds = mutableListOf<String>()
    val updatedCashBalances = mutableListOf<Double>()
    var initUserBalanceCalled = false
        private set

    fun setCashBalance(balance: Double) {
        cashBalanceFlow.value = balance
    }

    fun setHolding(coinId: String, holding: PortfolioHolding?) {
        if (holding == null) {
            holdingsState.remove(coinId)
        } else {
            holdingsState[coinId] = holding
        }
        holdingsRevision.value++
    }

    fun setSnapshotResult(result: Result<PortfolioSnapshot, DataError.Remote>?) {
        snapshotResult = result
    }

    fun applyBuy(holding: PortfolioHolding, amountInFiat: Double) {
        appliedBuyHoldings.add(holding)
        holdingsState[holding.coin.id] = holding
        cashBalanceFlow.value -= amountInFiat
        updatedCashBalances.add(cashBalanceFlow.value)
        holdingsRevision.value++
    }

    fun applySell(
        holdingUpdate: PortfolioHolding?,
        removeCoinId: String?,
        amountInFiat: Double,
    ) {
        appliedSellHoldings.add(holdingUpdate)
        when {
            removeCoinId != null -> {
                removedCoinIds.add(removeCoinId)
                holdingsState.remove(removeCoinId)
            }
            holdingUpdate != null -> holdingsState[holdingUpdate.coin.id] = holdingUpdate
        }
        cashBalanceFlow.value += amountInFiat
        updatedCashBalances.add(cashBalanceFlow.value)
        holdingsRevision.value++
    }

    override suspend fun initUserBalance() {
        initUserBalanceCalled = true
    }

    override fun observePortfolioSnapshot(): Flow<Result<PortfolioSnapshot, DataError.Remote>> {
        val override = snapshotResult
        if (override != null) {
            return flowOf(override)
        }
        return combine(cashBalanceFlow, holdingsRevision) { cash, _ ->
            buildSnapshot(cash)
        }
    }

    override suspend fun getPortfolioHolding(coinId: String): PortfolioHolding? =
        holdingsState[coinId]

    override fun observeCashBalance(): Flow<Double> = cashBalanceFlow

    private fun buildSnapshot(cashBalance: Double): Result<PortfolioSnapshot, DataError.Remote> {
        val coins = holdingsState.values.map { holding -> holding.toDisplayModel() }
        val portfolioMarketValue = coins.sumOf { it.marketValueFiat }
        return Result.Success(
            PortfolioSnapshot(
                coins = coins,
                cashBalance = cashBalance,
                portfolioMarketValue = portfolioMarketValue,
                totalBalance = cashBalance + portfolioMarketValue,
            ),
        )
    }

    private fun PortfolioHolding.toDisplayModel(): PortfolioCoinModel {
        val marketValue = ownedAmountInUnit * averagePurchasePrice
        return PortfolioCoinModel(
            coin = coin,
            performancePercent = 0.0,
            averagePurchasePrice = averagePurchasePrice,
            ownedAmountInUnit = ownedAmountInUnit,
            marketValueFiat = marketValue,
        )
    }
}
