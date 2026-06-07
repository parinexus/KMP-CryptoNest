package parinexus.kmp.first.portfolio.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.core.util.formatCoinUnit
import parinexus.kmp.first.core.util.formatFiat
import parinexus.kmp.first.core.util.formatPercentage
import parinexus.kmp.first.core.util.toUiText
import parinexus.kmp.first.portfolio.domain.PortfolioCoinModel
import parinexus.kmp.first.portfolio.domain.PortfolioRepository
import parinexus.kmp.first.portfolio.domain.PortfolioSnapshot

class PortfolioViewModel(
    private val portfolioRepository: PortfolioRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(PortfolioState(isLoading = true))
    val state: StateFlow<PortfolioState> = combine(
        _state,
        portfolioRepository.observePortfolioSnapshot(),
    ) { currentState, snapshotResult ->
        when (snapshotResult) {
            is Result.Success -> handleSuccessState(currentState, snapshotResult.data)
            is Result.Error -> handleErrorState(currentState, snapshotResult.error)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = PortfolioState(isLoading = true),
    )

    private fun handleSuccessState(
        currentState: PortfolioState,
        snapshot: PortfolioSnapshot,
    ): PortfolioState = currentState.copy(
        coins = snapshot.coins.map { it.toUiPortfolioCoinItem() },
        totalBalance = formatFiat(snapshot.totalBalance),
        holdingsValue = formatFiat(snapshot.portfolioMarketValue),
        cashBalance = formatFiat(snapshot.cashBalance),
        showBuyButton = snapshot.coins.isNotEmpty(),
        isLoading = false,
    )

    private fun handleErrorState(
        currentState: PortfolioState,
        error: DataError,
    ): PortfolioState = currentState.copy(
        isLoading = false,
        error = error.toUiText(),
    )

    private fun PortfolioCoinModel.toUiPortfolioCoinItem(): UiPortfolioCoinItem =
        UiPortfolioCoinItem(
            id = coin.id,
            name = coin.name,
            iconUrl = coin.iconUrl,
            amountInUnitText = formatCoinUnit(ownedAmountInUnit, coin.symbol),
            amountInFiatText = formatFiat(marketValueFiat),
            performancePercentText = formatPercentage(performancePercent),
            isPositive = performancePercent >= 0,
        )
}
