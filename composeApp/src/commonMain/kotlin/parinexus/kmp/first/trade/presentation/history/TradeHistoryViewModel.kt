package parinexus.kmp.first.trade.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import parinexus.kmp.first.trade.domain.ObserveTradeHistoryUseCase

class TradeHistoryViewModel(
    observeTradeHistoryUseCase: ObserveTradeHistoryUseCase,
) : ViewModel() {

    val state: StateFlow<TradeHistoryState> = observeTradeHistoryUseCase()
        .map { records ->
            TradeHistoryState(
                trades = records.map { it.toUiTradeHistoryItem() },
                isLoading = false,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TradeHistoryState(isLoading = true),
        )
}
