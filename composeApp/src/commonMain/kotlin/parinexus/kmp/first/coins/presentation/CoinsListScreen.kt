package parinexus.kmp.first.coins.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kmp_cryptonest.composeapp.generated.resources.Res
import kmp_cryptonest.composeapp.generated.resources.coins_grid_chart_badge
import kmp_cryptonest.composeapp.generated.resources.coins_grid_hold_for_chart
import kmp_cryptonest.composeapp.generated.resources.coins_list_interaction_hint
import org.jetbrains.compose.resources.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import parinexus.kmp.first.coins.presentation.component.PerformanceChart
import parinexus.kmp.first.core.presentation.component.ApiContentStateLayout
import parinexus.kmp.first.core.testing.CoinTestTags
import parinexus.kmp.first.theme.LocalCoinColorsPalette

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CoinsGridScreen(
    onCoinClicked: (String) -> Unit,
) {
    val coinViewModel = koinViewModel<CoinsListViewModel>()
    val state by coinViewModel.state.collectAsStateWithLifecycle()

    state.chartState?.let { chartState ->
        CoinChartDialog(
            uiChartState = chartState,
            onDismiss = { coinViewModel.onDismissChart() },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Crypto Dashboard",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag(CoinTestTags.COINS_DASHBOARD_TITLE),
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
            windowInsets = TopAppBarDefaults.windowInsets,
        )

        ApiContentStateLayout(
            isLoading = state.content is CoinsListContent.Loading,
            errorMessage = (state.content as? CoinsListContent.Error)?.message,
            isEmpty = state.content is CoinsListContent.Empty,
            emptyMessage = "No coins available right now.",
            onRetry = { coinViewModel.onRetryLoadCoins() },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .then(
                    when (state.content) {
                        is CoinsListContent.Loading -> Modifier.testTag(CoinTestTags.COINS_LOADING)
                        is CoinsListContent.Error -> Modifier.testTag(CoinTestTags.COINS_ERROR)
                        is CoinsListContent.Empty -> Modifier.testTag(CoinTestTags.COINS_EMPTY)
                        else -> Modifier
                    },
                ),
        ) {
            val coins = (state.content as CoinsListContent.Success).coins
            val density = LocalDensity.current
            val bottomInset = with(density) {
                WindowInsets.navigationBars.getBottom(this).toDp()
            }
            val gridContentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 16.dp + bottomInset,
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = gridContentPadding,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    CoinsListInteractionHint()
                }
                items(coins, key = { it.id }) { coin ->
                    CoinGridItem(
                        coin = coin,
                        onCoinClicked = onCoinClicked,
                        onCoinLongPressed = { coinId -> coinViewModel.onCoinLongPressed(coinId) },
                    )
                }
            }
        }
    }
}

@Composable
fun CoinsListInteractionHint() {
    val hint = stringResource(Res.string.coins_list_interaction_hint)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CoinTestTags.COINS_LIST_INTERACTION_HINT),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
    ) {
        Text(
            text = hint,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Medium,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CoinGridItem(
    coin: CoinUiModel,
    onCoinClicked: (String) -> Unit,
    onCoinLongPressed: (String) -> Unit,
) {
    val holdHint = stringResource(Res.string.coins_grid_hold_for_chart)
    val chartBadge = stringResource(Res.string.coins_grid_chart_badge)
    val itemDescription = "${coin.name}. Tap for details. $holdHint."

    Surface(
        modifier = Modifier
            .testTag(CoinTestTags.coinGridItem(coin.id))
            .fillMaxWidth()
            .semantics {
                contentDescription = itemDescription
                customActions = listOf(
                    CustomAccessibilityAction(holdHint) {
                        onCoinLongPressed(coin.id)
                        true
                    },
                )
            }
            .combinedClickable(
                onLongClick = { onCoinLongPressed(coin.id) },
                onClick = { onCoinClicked(coin.id) },
            ),
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 4.dp,
        shadowElevation = 6.dp,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                Text(
                    text = chartBadge,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }

            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
            AsyncImage(
                model = coin.iconUrl,
                contentDescription = "${coin.name} logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = coin.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = coin.symbol,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = coin.formattedPrice,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (coin.isPositive) {
                            LocalCoinColorsPalette.current.profitGreen.copy(alpha = 0.15f)
                        } else {
                            LocalCoinColorsPalette.current.lossRed.copy(alpha = 0.15f)
                        },
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    text = coin.formattedChange,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (coin.isPositive) {
                        LocalCoinColorsPalette.current.profitGreen
                    } else {
                        LocalCoinColorsPalette.current.lossRed
                    },
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag(CoinTestTags.COINS_GRID_HOLD_HINT),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "📈",
                    style = MaterialTheme.typography.labelMedium,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = holdHint,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                )
            }
            }
        }
    }
}

@Preview
@Composable
private fun CoinsGridPreview() {
    CoinsGridScreen(
        onCoinClicked = {},
    )
}

@Composable
fun CoinChartDialog(
    uiChartState: UiChartState,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CoinTestTags.COIN_CHART_DIALOG),
        onDismissRequest = onDismiss,
        title = {
            Text(text = "24h Price chart for ${uiChartState.coinName}")
        },
        text = {
            when {
                uiChartState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                }
                uiChartState.errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = uiChartState.errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                else -> {
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
            }
        },
        confirmButton = {},
        dismissButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag(CoinTestTags.COIN_CHART_CLOSE),
            ) {
                Text(text = "Close")
            }
        },
    )
}
