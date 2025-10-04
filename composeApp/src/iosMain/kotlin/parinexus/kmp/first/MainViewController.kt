package parinexus.kmp.first

import androidx.compose.ui.window.ComposeUIViewController
import parinexus.kmp.first.di.initKoin
import platform.Foundation.NSBundle

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    App()

    println("Secrets path: " + NSBundle.mainBundle.pathForResource("Secrets", "plist"))
}