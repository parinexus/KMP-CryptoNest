package parinexus.kmp.first.coins.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import parinexus.kmp.first.coins.domain.FetchCoinDetailsUseCase
import parinexus.kmp.first.coins.domain.FetchCoinPriceHistoryUseCase
import parinexus.kmp.first.core.api.presentation.RemoteErrorContext
import parinexus.kmp.first.core.api.presentation.RemoteErrorUiMapper
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.core.network.NetworkLogger

class CoinDetailViewModel(
    private val fetchCoinDetailsUseCase: FetchCoinDetailsUseCase,
    private val fetchCoinPriceHistoryUseCase: FetchCoinPriceHistoryUseCase,
    private val coinId: String,
) : ViewModel() {

    private val _state = MutableStateFlow(CoinDetailState())
    val state = _state
        .onStart { loadDetails() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CoinDetailState(content = CoinDetailContent.Loading),
        )

    fun onRetry() {
        viewModelScope.launch { loadDetails() }
    }

    fun onRetryChart() {
        viewModelScope.launch { loadChart() }
    }

    private suspend fun loadDetails() {
        _state.update {
            it.copy(
                content = CoinDetailContent.Loading,
                chartState = CoinDetailChartState.Loading,
            )
        }

        when (val result = fetchCoinDetailsUseCase.execute(coinId)) {
            is Result.Success -> {
                NetworkLogger.d("CoinDetailViewModel: loaded ${result.data.coin.name}")
                _state.update {
                    it.copy(
                        content = CoinDetailContent.Success(
                            detail = CoinDetailUiMapper.toUiModel(result.data),
                        ),
                        chartState = CoinDetailChartState.Loading,
                    )
                }
                loadChart()
            }
            is Result.Error -> {
                NetworkLogger.e(
                    "CoinDetailViewModel: load failed — ${result.error}, message=${result.message}",
                )
                _state.update {
                    it.copy(
                        content = CoinDetailContent.Error(
                            message = RemoteErrorUiMapper.toDisplayMessage(
                                result,
                                RemoteErrorContext.CoinDetail,
                            ),
                        ),
                    )
                }
            }
        }
    }

    private suspend fun loadChart() {
        if (_state.value.content !is CoinDetailContent.Success) return

        _state.update {
            if (it.content is CoinDetailContent.Success) {
                it.copy(chartState = CoinDetailChartState.Loading)
            } else {
                it
            }
        }

        when (val result = fetchCoinPriceHistoryUseCase.execute(coinId)) {
            is Result.Success -> {
                val sparkline = result.data
                    .sortedBy { it.timestamp }
                    .map { it.price }
                    .filter { it > 0.0 }

                _state.update { current ->
                    if (current.content !is CoinDetailContent.Success) return@update current
                    current.copy(
                        chartState = if (sparkline.size < 2) {
                            CoinDetailChartState.Empty(
                                message = "24h chart could not be loaded. Try Retry below.",
                            )
                        } else {
                            CoinDetailChartState.Ready(sparkline)
                        },
                    )
                }
            }
            is Result.Error -> {
                NetworkLogger.e(
                    "CoinDetailViewModel: chart failed — ${result.error}, message=${result.message}",
                )
                _state.update { current ->
                    if (current.content !is CoinDetailContent.Success) return@update current
                    current.copy(
                        chartState = CoinDetailChartState.Error(
                            message = RemoteErrorUiMapper.toDisplayMessage(
                                result,
                                RemoteErrorContext.CoinDetailChart,
                            ),
                        ),
                    )
                }
            }
        }
    }
}
