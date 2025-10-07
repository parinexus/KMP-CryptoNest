package parinexus.kmp.first.di

import androidx.room.RoomDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import parinexus.kmp.first.core.database.getPortfolioDatabaseBuilder
import parinexus.kmp.first.core.database.portfolio.PortfolioDatabase

actual val platformModule = module {

    // core
    single<HttpClientEngine> { Android.create() }
    singleOf(::getPortfolioDatabaseBuilder).bind<RoomDatabase.Builder<PortfolioDatabase>>()
}