package parinexus.kmp.first

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import parinexus.kmp.first.coins.presentation.CoinsGridScreen
import parinexus.kmp.first.coins.presentation.detail.CoinDetailScreen
import parinexus.kmp.first.portfolio.presentation.PortfolioScreen
import parinexus.kmp.first.theme.CoinTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import parinexus.kmp.first.core.navigation.Buy
import parinexus.kmp.first.core.navigation.CoinDetail
import parinexus.kmp.first.core.navigation.Coins
import parinexus.kmp.first.core.navigation.Portfolio
import parinexus.kmp.first.core.navigation.Sell
import parinexus.kmp.first.core.navigation.TradeHistory
import parinexus.kmp.first.trade.presentation.buy.BuyScreen
import parinexus.kmp.first.trade.presentation.history.TradeHistoryScreen
import parinexus.kmp.first.trade.presentation.sell.SellScreen

@Composable
@Preview
fun App() {
    val navController: NavHostController = rememberNavController()

    CoinTheme {
        SetSystemBarsColor(
            statusBarColor = MaterialTheme.colorScheme.primary,
            navigationBarColor = MaterialTheme.colorScheme.background,
            darkIcons = true
        )

        NavHost(
            navController = navController,
            startDestination = Portfolio,
            modifier = Modifier.fillMaxSize()
        ) {
            composable<Portfolio> {
                PortfolioScreen(
                    onCoinItemClicked = { coinId ->
                        navController.navigate(CoinDetail(coinId = coinId))
                    },
                    onDiscoverCoinsClicked = {
                        navController.navigate(Coins)
                    },
                    onTradeHistoryClicked = {
                        navController.navigate(TradeHistory)
                    },
                )
            }

            composable<TradeHistory> {
                TradeHistoryScreen(
                    onNavigateBack = { navController.popBackStack() },
                )
            }

            composable<Coins> {
                CoinsGridScreen { coinId ->
                    navController.navigate(CoinDetail(coinId = coinId))
                }
            }

            composable<CoinDetail> { navBackStackEntry ->
                val coinId = navBackStackEntry.toRoute<CoinDetail>().coinId
                CoinDetailScreen(
                    coinId = coinId,
                    onNavigateBack = { navController.popBackStack() },
                    onBuyClicked = { id -> navController.navigate(Buy(coinId = id)) },
                    onSellClicked = { id -> navController.navigate(Sell(coinId = id)) },
                )
            }

            composable<Buy> { navBackStackEntry ->
                val coinId = navBackStackEntry.toRoute<Buy>().coinId
                BuyScreen(
                    coinId = coinId,
                    navigateToPortfolio = {
                        navController.navigate(Portfolio) {
                            popUpTo(Portfolio) { inclusive = true }
                        }
                    }
                )
            }
            composable<Sell> { navBackStackEntry ->
                val coinId = navBackStackEntry.toRoute<Sell>().coinId

                SellScreen(
                    coinId = coinId,
                    navigateToPortfolio = {
                        navController.navigate(Portfolio) {
                            popUpTo(Portfolio) { inclusive = true }
                        }
                    }
                )
            }

        }
    }
}