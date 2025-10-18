package parinexus.kmp.first.trade.presentation.sell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import parinexus.kmp.first.coins.domain.FetchCoinDetailsUseCase
import parinexus.kmp.first.portfolio.domain.PortfolioRepository
import parinexus.kmp.first.trade.domain.SellCoinUseCase
import parinexus.kmp.first.trade.presentation.common.TradeState
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.core.util.formatFiat
import parinexus.kmp.first.core.util.toUiText
import parinexus.kmp.first.trade.presentation.common.UiTradeCoinItem
import parinexus.kmp.first.trade.presentation.mapper.toCoin

class SellViewModel(
    private val getCoinDetailsUseCase: FetchCoinDetailsUseCase,
    private val portfolioRepository: PortfolioRepository,
    private val sellCoinUseCase: SellCoinUseCase,
) : ViewModel() {

    private val tempCoinId = "1" // TODO: will be removed

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
        when (val portfolioCoinResponse = portfolioRepository.getPortfolioCoinById(tempCoinId)) {
            is Result.Success -> {
                portfolioCoinResponse.data?.ownedAmountInUnit?.let {
                    getCoinDetails(it)
                }
            }

            is Result.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = portfolioCoinResponse.error.toUiText()
                    )
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = TradeState(isLoading = true)
    )

    fun onAmountChanged(amount: String) {
        _amount.value = amount
    }

    private suspend fun getCoinDetails(ownedAmountInUnit: Double) {
        when (val coinResponse = getCoinDetailsUseCase.execute(tempCoinId)) {
            is Result.Success -> {
                val availableAmountInFiat = ownedAmountInUnit * coinResponse.data.price
                _state.update {
                    it.copy(
                        coin = UiTradeCoinItem(
                            id = coinResponse.data.coin.id,
                            name = coinResponse.data.coin.name,
                            symbol = coinResponse.data.coin.symbol,
                            iconUrl = coinResponse.data.coin.iconUrl,
                            price = coinResponse.data.price,
                        ),
                        availableAmount = "Available: ${formatFiat(availableAmountInFiat)}"
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

    fun onSellClicked() {
        val tradeCoin = state.value.coin ?: return
        viewModelScope.launch {
            val sellCoinResponse = sellCoinUseCase.sellCoin(
                coin = tradeCoin.toCoin(),
                amountInFiat = _amount.value.toDouble(),
                price = tradeCoin.price
            )
            when (sellCoinResponse) {
                is Result.Success -> {
                    // TODO: add event and navigation
                }

                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = sellCoinResponse.error.toUiText()
                        )
                    }
                }
            }
        }
    }
}