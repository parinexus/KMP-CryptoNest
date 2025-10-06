package parinexus.kmp.first.coins.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import parinexus.kmp.first.coins.domain.FetchCoinsListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import parinexus.kmp.first.core.domain.Result
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import parinexus.kmp.first.coins.domain.FetchCoinPriceHistoryUseCase
import parinexus.kmp.first.core.util.formatFiat
import parinexus.kmp.first.core.util.formatPercentage
import parinexus.kmp.first.core.util.toUiText

class CoinsListViewModel(
    private val fetchCoinsListUseCase: FetchCoinsListUseCase,
    private val fetchCoinPriceHistoryUseCase: FetchCoinPriceHistoryUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CoinsState())
    val state = _state
        .onStart {
            loadCoins()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CoinsState()
        )

    private suspend fun loadCoins() {
        _state.update { it.copy(isLoading = true) }

        when (val response = fetchCoinsListUseCase.execute()) {
            is Result.Success -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        coins = response.data.map { coin ->
                            CoinUiModel(
                                id = coin.coin.id,
                                name = coin.coin.name,
                                iconUrl = coin.coin.iconUrl,
                                symbol = coin.coin.symbol,
                                formattedPrice = formatFiat(coin.price),
                                formattedChange = formatPercentage(coin.changePercent),
                                isPositive = coin.changePercent >= 0
                            )
                        },
                        error = null
                    )
                }
            }

            is Result.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        coins = emptyList(),
                        error = response.error.toUiText()
                    )
                }
            }
        }
    }

    fun onCoinLongPressed(coinId: String) {
        _state.update {
            it.copy(
                chartState = UiChartState(
                    sparkLine = emptyList(),
                    isLoading = true,
                )
            )
        }

        viewModelScope.launch {
            when(val priceHistory = fetchCoinPriceHistoryUseCase.execute(coinId)) {
                is Result.Success -> {
                    _state.update { currentState ->
                        currentState.copy(
                            chartState = UiChartState(
                                sparkLine = priceHistory.data.sortedBy { it.timestamp }.map { it.price },
                                isLoading = false,
                                coinName = _state.value.coins.find { it.id == coinId }?.name.orEmpty(),
                            )
                        )
                    }
                }
                is Result.Error -> {
                    _state.update { currentState ->
                        currentState.copy(
                            chartState = UiChartState(
                                sparkLine = emptyList(),
                                isLoading = false,
                                coinName = "",
                            )
                        )
                    }
                }
            }
        }
    }

    fun onDismissChart() {
        _state.update {
            it.copy(chartState = null)
        }
    }
}