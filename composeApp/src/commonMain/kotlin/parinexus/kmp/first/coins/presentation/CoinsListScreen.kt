package parinexus.kmp.first.coins.presentation

import org.jetbrains.compose.ui.tooling.preview.Preview
import parinexus.kmp.first.theme.LocalCoinColorsPalette
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CoinsGridScreen(
    coins: List<CoinUiModel>,
    onCoinClicked: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
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

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(coins, key = { it.id }) { coin ->
                CoinGridItem(
                    coin = coin,
                    onCoinClicked = onCoinClicked
                )
            }
        }
    }
}

@Composable
fun CoinGridItem(
    coin: CoinUiModel,
    onCoinClicked: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCoinClicked(coin.id) },
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
        coins = listOf(
            CoinUiModel("btc", "Bitcoin", "BTC", "", "$65,000", "+2.5%", true),
            CoinUiModel("eth", "Ethereum", "ETH", "", "$3,500", "-1.2%", false),
            CoinUiModel("sol", "Solana", "SOL", "", "$180", "+5.8%", true),
            CoinUiModel("ada", "Cardano", "ADA", "", "$0.45", "-0.7%", false),
        ),
        onCoinClicked = {}
    )
}