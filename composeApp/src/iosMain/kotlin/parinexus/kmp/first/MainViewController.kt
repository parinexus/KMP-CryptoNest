package parinexus.kmp.first

import androidx.compose.ui.window.ComposeUIViewController
import parinexus.kmp.first.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    },
) {
    App()
}
