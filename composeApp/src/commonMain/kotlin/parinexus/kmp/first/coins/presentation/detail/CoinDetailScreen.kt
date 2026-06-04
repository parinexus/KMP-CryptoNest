package parinexus.kmp.first.coins.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import parinexus.kmp.first.coins.presentation.component.PerformanceChart
import parinexus.kmp.first.core.presentation.component.ApiContentStateLayout
import parinexus.kmp.first.core.presentation.component.MarketCacheBanner
import parinexus.kmp.first.core.testing.CoinTestTags
import parinexus.kmp.first.theme.LocalCoinColorsPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinDetailScreen(
    coinId: String,
    onNavigateBack: () -> Unit,
    onBuyClicked: (String) -> Unit,
    onSellClicked: (String) -> Unit,
) {
    val viewModel = koinViewModel<CoinDetailViewModel>(
        parameters = { parametersOf(coinId) },
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Coin details",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.testTag(CoinTestTags.COIN_DETAIL_TITLE),
                    )
                },
                navigationIcon = {
                    TextButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag(CoinTestTags.COIN_DETAIL_BACK),
                    ) {
                        Text(
                            text = "Back",
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
            )
        },
        modifier = Modifier.navigationBarsPadding(),
    ) { paddingValues ->
        val contentModifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)

        when (val content = state.content) {
            is CoinDetailContent.Loading -> {
                ApiContentStateLayout(
                    isLoading = true,
                    errorMessage = null,
                    isEmpty = false,
                    emptyMessage = "",
                    onRetry = null,
                    modifier = contentModifier.testTag(CoinTestTags.COIN_DETAIL_LOADING),
                ) {}
            }
            is CoinDetailContent.Error -> {
                ApiContentStateLayout(
                    isLoading = false,
                    errorMessage = content.message,
                    isEmpty = false,
                    emptyMessage = "",
                    onRetry = viewModel::onRetry,
                    modifier = contentModifier.testTag(CoinTestTags.COIN_DETAIL_ERROR),
                ) {}
            }
            is CoinDetailContent.Success -> {
                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { viewModel.onRefresh() },
                    modifier = contentModifier.testTag(CoinTestTags.COIN_DETAIL_SUCCESS),
                ) {
                    CoinDetailBody(
                        detail = content.detail,
                        chartState = state.chartState,
                        cacheBanner = state.cacheBanner,
                        onBuyClicked = { onBuyClicked(content.detail.coinId) },
                        onSellClicked = { onSellClicked(content.detail.coinId) },
                        onRetryChart = viewModel::onRetryChart,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CoinDetailBody(
    detail: CoinDetailUiModel,
    chartState: CoinDetailChartState,
    cacheBanner: org.jetbrains.compose.resources.StringResource?,
    onBuyClicked: () -> Unit,
    onSellClicked: () -> Unit,
    onRetryChart: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val palette = LocalCoinColorsPalette.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        cacheBanner?.let { banner ->
            MarketCacheBanner(
                message = banner,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(CoinTestTags.COIN_DETAIL_CACHE_BANNER),
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            AsyncImage(
                model = detail.iconUrl,
                contentDescription = "${detail.name} logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .testTag(CoinTestTags.COIN_DETAIL_ICON),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = detail.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag(CoinTestTags.COIN_DETAIL_NAME),
                )
                Text(
                    text = detail.symbol,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "Rank ${detail.rankLabel}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = detail.formattedPrice,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag(CoinTestTags.COIN_DETAIL_PRICE),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(50),
            color = if (detail.isPositiveChange) {
                palette.profitGreen.copy(alpha = 0.15f)
            } else {
                palette.lossRed.copy(alpha = 0.15f)
            },
        ) {
            Text(
                text = "${detail.formattedChange} (24h)",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelLarge,
                color = if (detail.isPositiveChange) palette.profitGreen else palette.lossRed,
            )
        }

        if (detail.notices.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            detail.notices.forEach { notice ->
                CoinNoticeBanner(notice = notice)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        CoinDetailChartCard(
            chartState = chartState,
            profitColor = palette.profitGreen,
            lossColor = palette.lossRed,
            onRetryChart = onRetryChart,
        )

        Spacer(modifier = Modifier.height(20.dp))

        StatsSection(detail = detail)

        if (detail.tags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tags",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                detail.tags.forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    ) {
                        Text(
                            text = tag,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "About",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = detail.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.testTag(CoinTestTags.COIN_DETAIL_DESCRIPTION),
        )

        if (!detail.websiteUrl.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = detail.websiteUrl,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FilledTonalButton(
                onClick = onBuyClicked,
                modifier = Modifier
                    .weight(1f)
                    .testTag(CoinTestTags.COIN_DETAIL_BUY),
            ) {
                Text(text = "Buy")
            }
            OutlinedButton(
                onClick = onSellClicked,
                modifier = Modifier
                    .weight(1f)
                    .testTag(CoinTestTags.COIN_DETAIL_SELL),
            ) {
                Text(text = "Sell")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StatsSection(detail: CoinDetailUiModel) {
    Text(
        text = "Market stats",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
    )
    Spacer(modifier = Modifier.height(12.dp))
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        StatRow(label = "Market cap", value = detail.marketCapLabel)
        StatRow(label = "24h volume", value = detail.volume24hLabel)
        StatRow(label = "Markets", value = detail.marketsLabel)
        StatRow(label = "Exchanges", value = detail.exchangesLabel)
        StatRow(label = "Supply (circ. / max)", value = detail.supplyLabel)
        StatRow(label = "All-time high", value = detail.allTimeHighLabel)
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun CoinNoticeBanner(notice: CoinNoticeUiModel) {
    val colors = when (notice.style) {
        CoinNoticeStyle.Info -> NoticeBannerColors(
            container = MaterialTheme.colorScheme.secondaryContainer,
            content = MaterialTheme.colorScheme.onSecondaryContainer,
        )
        CoinNoticeStyle.Warning -> NoticeBannerColors(
            container = MaterialTheme.colorScheme.tertiaryContainer,
            content = MaterialTheme.colorScheme.onTertiaryContainer,
        )
        CoinNoticeStyle.Alert -> NoticeBannerColors(
            container = MaterialTheme.colorScheme.errorContainer,
            content = MaterialTheme.colorScheme.onErrorContainer,
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CoinTestTags.COIN_DETAIL_NOTICE),
        shape = RoundedCornerShape(12.dp),
        color = colors.container,
    ) {
        CoinNoticeText(
            segments = notice.segments,
            modifier = Modifier.padding(12.dp),
            textColor = colors.content,
            linkColor = MaterialTheme.colorScheme.primary,
        )
    }
}

private data class NoticeBannerColors(
    val container: Color,
    val content: Color,
)

@Composable
private fun CoinDetailChartCard(
    chartState: CoinDetailChartState,
    profitColor: Color,
    lossColor: Color,
    onRetryChart: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .testTag(CoinTestTags.COIN_DETAIL_CHART),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        ),
    ) {
        when (chartState) {
            is CoinDetailChartState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                }
            }
            is CoinDetailChartState.Ready -> {
                PerformanceChart(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    nodes = chartState.sparkline,
                    profitColor = profitColor,
                    lossColor = lossColor,
                )
            }
            is CoinDetailChartState.Error -> {
                ChartMessageState(
                    message = chartState.message,
                    actionLabel = "Retry",
                    onAction = onRetryChart,
                    modifier = Modifier.testTag(CoinTestTags.COIN_DETAIL_CHART_ERROR),
                )
            }
            is CoinDetailChartState.Empty -> {
                ChartMessageState(message = chartState.message)
            }
        }
    }
}

@Composable
private fun ChartMessageState(
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            if (actionLabel != null && onAction != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onAction) {
                    Text(text = actionLabel)
                }
            }
        }
    }
}

