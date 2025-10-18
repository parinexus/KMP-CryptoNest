package parinexus.kmp.first.trade.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import parinexus.kmp.first.theme.LocalCoinColorsPalette
import parinexus.kmp.first.trade.presentation.common.component.rememberCurrencyVisualTransformation

enum class TradeType {
    BUY, SELL
}

@Composable
fun TradeScreen(
    state: TradeState,
    tradeType: TradeType,
    onAmountChange: (String) -> Unit,
    onSubmitClicked: () -> Unit
) {
    val colors = LocalCoinColorsPalette.current
    val isBuy = tradeType == TradeType.BUY

    val actionColor = if (isBuy) colors.profitGreen else colors.lossRed
    val titleText = if (isBuy) "Buy Amount" else "Sell Amount"
    val buttonText = if (isBuy) "Buy Now" else "Sell Now"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                AsyncImage(
                    model = state.coin?.iconUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = state.coin?.name ?: "Coin",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = titleText,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onBackground
            )

            CenteredDollarTextField(amountText = "amountText", onAmountChange = {})

            Text(
                text = "Available: ${state.availableAmount}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (state.error != null) {
                Text(
                    text = stringResource(resource = state.error),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.lossRed,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Button(
            onClick = onSubmitClicked,
            colors = ButtonDefaults.buttonColors(
                containerColor = actionColor,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .fillMaxWidth(0.9f)
                .height(54.dp)
        ) {
            Text(
                text = buttonText,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )
        }
    }
}


@Preview
@Composable
fun PreviewTradeScreenBuy() {
    val mockState = TradeState(
        coin = UiTradeCoinItem(
            symbol = "Bitcoin",
            id = "Bitcoin",
            name = "Bitcoin",
            iconUrl = "https://cryptologos.cc/logos/bitcoin-btc-logo.png",
            price = 34.7
        ),
        amount = "",
        availableAmount = "$150.00"
    )

    TradeScreen(
        state = mockState,
        tradeType = TradeType.BUY,
        onAmountChange = {},
        onSubmitClicked = {}
    )
}

@Preview
@Composable
fun PreviewTradeScreenSell() {
    val mockState = TradeState(
        coin = UiTradeCoinItem(
            id = "Cardano",
            symbol = "Cardano",
            name = "Cardano",
            iconUrl = "https://cryptologos.cc/logos/cardano-ada-logo.png",
            price = 10.2
        ),
        amount = "",
        availableAmount = "$25.23"
    )

    TradeScreen(
        state = mockState,
        tradeType = TradeType.SELL,
        onAmountChange = {},
        onSubmitClicked = {}
    )
}



@Composable
fun CenteredDollarTextField(
    modifier: Modifier = Modifier,
    amountText: String,
    onAmountChange: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val displayText = amountText.trimStart('$')
    val currencyVisualTransformation = rememberCurrencyVisualTransformation()

    BasicTextField(
        value = displayText,
        onValueChange = { newValue ->
            val trimmed = newValue.trimStart('0').trim { it.isDigit().not() }
            if (trimmed.isEmpty() || trimmed.toInt() <= 10000) {
                onAmountChange(trimmed)
            }
        },
        modifier = modifier
            .focusRequester(focusRequester)
            .padding(16.dp),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.height(56.dp).wrapContentWidth()
            ) {
                innerTextField()
            }
        },
        cursorBrush = SolidColor(Color.White),
        visualTransformation = currencyVisualTransformation,
    )
}

