package parinexus.kmp.first.coins.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import parinexus.kmp.first.core.testing.CoinTestTags
import parinexus.kmp.first.testing.ComposeHostActivity
import parinexus.kmp.first.theme.CoinTheme

@RunWith(AndroidJUnit4::class)
class CoinChartDialogUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComposeHostActivity>()

    @Test
    fun chartDialog_showsTitleAndCloseButton() {
        composeRule.setContent {
            CoinTheme {
                CoinChartDialog(
                    uiChartState = UiChartState(
                        sparkLine = listOf(1.0, 2.0, 3.0),
                        isLoading = false,
                        coinName = "Bitcoin",
                    ),
                    onDismiss = {},
                )
            }
        }

        composeRule.onNodeWithTag(CoinTestTags.COIN_CHART_DIALOG).assertIsDisplayed()
        composeRule.onNodeWithText("24h Price chart for Bitcoin").assertIsDisplayed()
        composeRule.onNodeWithTag(CoinTestTags.COIN_CHART_CLOSE).assertIsDisplayed()
    }

    @Test
    fun chartDialog_closeButtonInvokesDismiss() {
        var dismissed = false

        composeRule.setContent {
            CoinTheme {
                CoinChartDialog(
                    uiChartState = UiChartState(isLoading = true),
                    onDismiss = { dismissed = true },
                )
            }
        }

        composeRule.onNodeWithTag(CoinTestTags.COIN_CHART_CLOSE).performClick()

        composeRule.runOnIdle {
            assert(dismissed)
        }
    }

    @Test
    fun chartDialog_showsLoadingIndicator() {
        composeRule.setContent {
            CoinTheme {
                CoinChartDialog(
                    uiChartState = UiChartState(isLoading = true, coinName = "Bitcoin"),
                    onDismiss = {},
                )
            }
        }

        composeRule.onNodeWithTag(CoinTestTags.COIN_CHART_DIALOG).assertIsDisplayed()
    }
}
