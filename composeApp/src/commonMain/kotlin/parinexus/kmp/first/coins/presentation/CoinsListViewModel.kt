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
import parinexus.kmp.first.core.util.formatFiat
import parinexus.kmp.first.core.util.formatPercentage
import parinexus.kmp.first.core.util.toUiText

class CoinsListViewModel(
    private val fetchCoinsListUseCase: FetchCoinsListUseCase,
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


    suspend fun loadCoins() {
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
}