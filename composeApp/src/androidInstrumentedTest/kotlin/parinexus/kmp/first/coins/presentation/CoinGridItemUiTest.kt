package parinexus.kmp.first.coins.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import parinexus.kmp.first.core.testing.CoinTestTags
import parinexus.kmp.first.test.fixture.TestCoins
import parinexus.kmp.first.testing.ComposeHostActivity
import parinexus.kmp.first.theme.CoinTheme

@RunWith(AndroidJUnit4::class)
class CoinGridItemUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComposeHostActivity>()

    @Test
    fun coinGridItem_displaysCoinDetails() {
        val coin = CoinUiModel(
            id = TestCoins.BITCOIN_ID,
            name = "Bitcoin",
            iconUrl = TestCoins.bitcoin.iconUrl,
            symbol = "BTC",
            formattedPrice = "$50,000.00",
            formattedChange = "+2.50%",
            isPositive = true,
        )

        composeRule.setContent {
            CoinTheme {
                CoinGridItem(
                    coin = coin,
                    onCoinClicked = {},
                    onCoinLongPressed = {},
                )
            }
        }

        composeRule.onNodeWithTag(CoinTestTags.coinGridItem(TestCoins.BITCOIN_ID)).assertIsDisplayed()
        composeRule.onNodeWithText("Bitcoin").assertIsDisplayed()
        composeRule.onNodeWithText("BTC").assertIsDisplayed()
        composeRule.onNodeWithText("$50,000.00").assertIsDisplayed()
        composeRule.onNodeWithText("+2.50%").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Bitcoin logo").assertIsDisplayed()
        composeRule.onNodeWithTag(CoinTestTags.COINS_GRID_HOLD_HINT).assertIsDisplayed()
        composeRule.onNodeWithText("Hold for 24h chart").assertIsDisplayed()
    }

    @Test
    fun coinGridItem_clickInvokesCallback() {
        var clickedId: String? = null
        val coin = CoinUiModel(
            id = TestCoins.ETHEREUM_ID,
            name = "Ethereum",
            iconUrl = TestCoins.ethereum.iconUrl,
            symbol = "ETH",
            formattedPrice = "$3,000.00",
            formattedChange = "-1.20%",
            isPositive = false,
        )

        composeRule.setContent {
            CoinTheme {
                CoinGridItem(
                    coin = coin,
                    onCoinClicked = { clickedId = it },
                    onCoinLongPressed = {},
                )
            }
        }

        composeRule.onNodeWithTag(CoinTestTags.coinGridItem(TestCoins.ETHEREUM_ID)).performClick()

        composeRule.runOnIdle {
            assert(clickedId == TestCoins.ETHEREUM_ID)
        }
    }
}
