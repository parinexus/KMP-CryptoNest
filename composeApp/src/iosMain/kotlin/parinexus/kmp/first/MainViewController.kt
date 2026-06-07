package parinexus.kmp.first

import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import parinexus.kmp.first.di.initKoin
import platform.Foundation.NSBundle

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            KoinPlatform.getKoin().get<AppInitializer>().initialize()
        }
    },
) {
    App()

    println("Secrets path: " + NSBundle.mainBundle.pathForResource("Secrets", "plist"))
}
