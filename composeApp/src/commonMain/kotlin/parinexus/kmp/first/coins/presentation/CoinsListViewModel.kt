package parinexus.kmp.first.coins.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import parinexus.kmp.first.coins.domain.FetchCoinPriceHistoryUseCase
import parinexus.kmp.first.coins.domain.FetchCoinsListUseCase
import parinexus.kmp.first.core.api.presentation.RemoteErrorUiMapper
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.core.network.NetworkLogger
import parinexus.kmp.first.core.util.formatFiat
import parinexus.kmp.first.core.util.formatPercentage

class CoinsListViewModel(
    private val fetchCoinsListUseCase: FetchCoinsListUseCase,
    private val fetchCoinPriceHistoryUseCase: FetchCoinPriceHistoryUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CoinsState())
    val state = _state
        .onStart { loadCoins() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CoinsState(content = CoinsListContent.Loading),
        )

    fun onRetryLoadCoins() {
        viewModelScope.launch { loadCoins() }
    }

    private suspend fun loadCoins() {
        _state.update { it.copy(content = CoinsListContent.Loading) }

        when (val response = fetchCoinsListUseCase.execute()) {
            is Result.Success -> {
                val uiCoins = response.data.map { coin ->
                    CoinUiModel(
                        id = coin.coin.id,
                        name = coin.coin.name,
                        iconUrl = coin.coin.iconUrl,
                        symbol = coin.coin.symbol,
                        formattedPrice = formatFiat(coin.price),
                        formattedChange = formatPercentage(coin.changePercent),
                        isPositive = coin.changePercent >= 0,
                    )
                }
                NetworkLogger.d("CoinsListViewModel: loaded ${uiCoins.size} coins")
                _state.update {
                    it.copy(
                        content = when {
                            uiCoins.isEmpty() -> CoinsListContent.Empty
                            else -> CoinsListContent.Success(coins = uiCoins)
                        },
                    )
                }
            }
            is Result.Error -> {
                NetworkLogger.e(
                    "CoinsListViewModel: load failed — ${response.error}, message=${response.message}",
                )
                _state.update {
                    it.copy(
                        content = CoinsListContent.Error(
                            message = RemoteErrorUiMapper.toDisplayMessage(response),
                        ),
                    )
                }
            }
        }
    }

    fun onCoinLongPressed(coinId: String) {
        val coinName = (_state.value.content as? CoinsListContent.Success)
            ?.coins
            ?.find { it.id == coinId }
            ?.name
            .orEmpty()

        _state.update {
            it.copy(
                chartState = UiChartState(
                    sparkLine = emptyList(),
                    isLoading = true,
                    coinName = coinName,
                ),
            )
        }

        viewModelScope.launch {
            when (val priceHistory = fetchCoinPriceHistoryUseCase.execute(coinId)) {
                is Result.Success -> {
                    val sparkLine = priceHistory.data
                        .sortedBy { it.timestamp }
                        .map { it.price }
                        .filter { it > 0.0 }

                    _state.update { currentState ->
                        currentState.copy(
                            chartState = UiChartState(
                                sparkLine = sparkLine,
                                isLoading = false,
                                coinName = coinName,
                                errorMessage = if (sparkLine.isEmpty()) {
                                    "No price data for the last 24 hours."
                                } else {
                                    null
                                },
                            ),
                        )
                    }
                }
                is Result.Error -> {
                    NetworkLogger.e(
                        "CoinsListViewModel: chart failed for $coinName ($coinId) — " +
                            "${priceHistory.error}, message=${priceHistory.message}",
                    )
                    _state.update { currentState ->
                        currentState.copy(
                            chartState = UiChartState(
                                sparkLine = emptyList(),
                                isLoading = false,
                                coinName = coinName,
                                errorMessage = RemoteErrorUiMapper.toDisplayMessage(priceHistory),
                            ),
                        )
                    }
                }
            }
        }
    }

    fun onDismissChart() {
        _state.update { it.copy(chartState = null) }
    }
}
