package parinexus.kmp.first

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import parinexus.kmp.first.di.initKoin

class CoinApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@CoinApplication)
        }
    }
}
