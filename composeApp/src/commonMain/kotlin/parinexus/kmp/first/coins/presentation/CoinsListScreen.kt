package parinexus.kmp.first.coins.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import parinexus.kmp.first.coins.presentation.component.PerformanceChart
import parinexus.kmp.first.theme.LocalCoinColorsPalette

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CoinsGridScreen(
    onCoinClicked: (String) -> Unit,
) {
    val coinViewModel = koinViewModel<CoinsListViewModel>()
    val state by coinViewModel.state.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        state.chartState?.let {
            CoinChartDialog(
                uiChartState = it,
                onDismiss = { coinViewModel.onDismissChart() },
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = WindowInsets.systemBars.asPaddingValues(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Crypto Dashboard",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            items(state.coins, key = { it.id }) { coin ->
                CoinGridItem(
                    coin = coin,
                    onCoinClicked = onCoinClicked,
                    onCoinLongPressed = { coinId -> coinViewModel.onCoinLongPressed(coinId) },
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CoinGridItem(
    coin: CoinUiModel,
    onCoinClicked: (String) -> Unit,
    onCoinLongPressed: (String) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onLongClick = { onCoinLongPressed(coin.id) },
                onClick = { onCoinClicked(coin.id) }
            ),
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 4.dp,
        shadowElevation = 6.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = coin.iconUrl,
                contentDescription = "${coin.name} logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = coin.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = coin.symbol,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = coin.formattedPrice,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (coin.isPositive) LocalCoinColorsPalette.current.profitGreen.copy(alpha = 0.15f)
                        else LocalCoinColorsPalette.current.lossRed.copy(alpha = 0.15f)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = coin.formattedChange,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (coin.isPositive) LocalCoinColorsPalette.current.profitGreen
                    else LocalCoinColorsPalette.current.lossRed
                )
            }
        }
    }
}

@Preview
@Composable
private fun CoinsGridPreview() {
    CoinsGridScreen(
        onCoinClicked = {}
    )
}

@Composable
fun CoinChartDialog(
    uiChartState: UiChartState,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "24h Price chart for ${uiChartState.coinName}",
            )
        },
        text = {
            if (uiChartState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                }
            } else {
                PerformanceChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp),
                    nodes = uiChartState.sparkLine,
                    profitColor = LocalCoinColorsPalette.current.profitGreen,
                    lossColor = LocalCoinColorsPalette.current.lossRed,
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(
                    text = "Close",
                )
            }
        }
    )
}