package parinexus.kmp.first.coins.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
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
import parinexus.kmp.first.core.domain.cache.CachedData
import parinexus.kmp.first.core.network.NetworkLogger
import kmp_cryptonest.composeapp.generated.resources.Res
import kmp_cryptonest.composeapp.generated.resources.market_cache_refresh_failed
import parinexus.kmp.first.core.presentation.cache.DataFreshnessUiMapper

class CoinDetailViewModel(
    private val fetchCoinDetailsUseCase: FetchCoinDetailsUseCase,
    private val fetchCoinPriceHistoryUseCase: FetchCoinPriceHistoryUseCase,
    private val coinId: String,
) : ViewModel() {

    private val _state = MutableStateFlow(CoinDetailState())
    private var loadJob: Job? = null

    val state = _state
        .onStart { loadDetails(forceRefresh = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CoinDetailState(content = CoinDetailContent.Loading),
        )

    fun onRetry() {
        loadDetails(forceRefresh = true)
    }

    fun onRefresh() {
        loadDetails(forceRefresh = true)
    }

    fun onRetryChart() {
        viewModelScope.launch { loadChart(forceRefresh = true) }
    }

    private fun loadDetails(forceRefresh: Boolean) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val hasContent = _state.value.content is CoinDetailContent.Success

            if (forceRefresh && hasContent) {
                _state.update { it.copy(isRefreshing = true) }
            } else if (!hasContent) {
                _state.update {
                    it.copy(
                        content = CoinDetailContent.Loading,
                        chartState = CoinDetailChartState.Loading,
                        cacheBanner = null,
                    )
                }
            }

            fetchCoinDetailsUseCase(coinId, forceRefresh).collect { result ->
                when (result) {
                    is Result.Success -> {
                        NetworkLogger.d("CoinDetailViewModel: loaded ${result.data.value.coin.name}")
                        _state.update {
                            it.copy(
                                content = CoinDetailContent.Success(
                                    detail = CoinDetailUiMapper.toUiModel(result.data.value),
                                ),
                                cacheBanner = DataFreshnessUiMapper.bannerMessage(result.data.freshness),
                                isRefreshing = false,
                                chartState = CoinDetailChartState.Loading,
                            )
                        }
                        loadChart(forceRefresh = forceRefresh)
                    }
                    is Result.Error -> {
                        if (_state.value.content !is CoinDetailContent.Success) {
                            _state.update {
                                it.copy(
                                    content = CoinDetailContent.Error(
                                        message = RemoteErrorUiMapper.toDisplayMessage(
                                            result,
                                            RemoteErrorContext.CoinDetail,
                                        ),
                                    ),
                                    isRefreshing = false,
                                )
                            }
                        } else {
                            _state.update {
                                it.copy(
                                    isRefreshing = false,
                                    cacheBanner = Res.string.market_cache_refresh_failed,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun loadChart(forceRefresh: Boolean) {
        if (_state.value.content !is CoinDetailContent.Success) return

        _state.update {
            if (it.content is CoinDetailContent.Success) {
                it.copy(chartState = CoinDetailChartState.Loading)
            } else {
                it
            }
        }

        when (val result = fetchCoinPriceHistoryUseCase.execute(coinId, forceRefresh)) {
            is Result.Success -> {
                val sparkline = result.data.value
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
