package parinexus.kmp.first.trade.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import parinexus.kmp.first.coins.domain.FetchCoinDetailsUseCase
import parinexus.kmp.first.portfolio.domain.PortfolioRepository
import parinexus.kmp.first.trade.domain.BuyCoinUseCase
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.core.util.formatFiat
import parinexus.kmp.first.core.util.toUiText
import parinexus.kmp.first.trade.presentation.common.TradeState
import parinexus.kmp.first.trade.presentation.common.UiTradeCoinItem
import parinexus.kmp.first.trade.presentation.mapper.toCoin

class BuyViewModel(
    private val getCoinDetailsUseCase: FetchCoinDetailsUseCase,
    private val portfolioRepository: PortfolioRepository,
    private val buyCoinUseCase: BuyCoinUseCase,
) : ViewModel() {

    private val tempCoinId = "1"
    private val _amount = MutableStateFlow("")
    private val _state = MutableStateFlow(TradeState())
    val state = combine(
        _state,
        _amount,
    ) { state, amount ->
        state.copy(
            amount = amount
        )
    }.onStart {
        val balance = portfolioRepository.totalCashBalanceFlow().first()
        getCoinDetails(balance)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = TradeState(isLoading = true)
    )

    private suspend fun getCoinDetails(balance: Double) {
        when (val coinResponse = getCoinDetailsUseCase.execute(tempCoinId)) {
            is Result.Success -> {
                _state.update {
                    it.copy(
                        coin = UiTradeCoinItem(
                            id = coinResponse.data.coin.id,
                            name = coinResponse.data.coin.name,
                            symbol = coinResponse.data.coin.symbol,
                            iconUrl = coinResponse.data.coin.iconUrl,
                            price = coinResponse.data.price,
                        ),
                        availableAmount = "Available: ${formatFiat(balance)}"
                    )
                }
            }

            is Result.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = coinResponse.error.toUiText()
                    )
                }
            }
        }
    }

    fun onAmountChanged(amount: String) {
        _amount.value = amount
    }

    fun onBuyClicked() {
        val tradeCoin = state.value.coin ?: return
        viewModelScope.launch {
            val buyCoinResponse = buyCoinUseCase.buyCoin(
                coin = tradeCoin.toCoin(),
                amountInFiat = _amount.value.toDouble(),
                price = tradeCoin.price,
            )

            when(buyCoinResponse) {
                is Result.Success -> {
                    // TODO: Navigate to next screen with event
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = buyCoinResponse.error.toUiText(),
                        )
                    }
                }
            }
        }

    }
}