package parinexus.kmp.first.coins.presentation

import androidx.compose.runtime.Stable
import org.jetbrains.compose.resources.StringResource

@Stable
data class CoinsState(
    val isLoading: Boolean = false,
    val error: StringResource? = null,
    val coins: List<CoinUiModel> = emptyList(),
)