package parinexus.kmp.first

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import parinexus.kmp.first.coins.presentation.CoinsGridScreen
import parinexus.kmp.first.portfolio.presentation.PortfolioScreen
import parinexus.kmp.first.theme.CoinTheme

@Composable
@Preview
fun App() {
    CoinTheme {
//        CoinsGridScreen({})
        PortfolioScreen(
            onCoinItemClicked = {},
            onDiscoverCoinsClicked = {}
        )
    }
}