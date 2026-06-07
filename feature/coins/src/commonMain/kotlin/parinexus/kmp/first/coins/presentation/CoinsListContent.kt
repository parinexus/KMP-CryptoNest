package parinexus.kmp.first.coins.presentation

import androidx.compose.runtime.Stable

@Stable
sealed interface CoinsListContent {
    data object Loading : CoinsListContent

    data class Success(
        val coins: List<CoinUiModel>,
    ) : CoinsListContent

    data class Error(
        val message: String,
    ) : CoinsListContent

    data object Empty : CoinsListContent
}
