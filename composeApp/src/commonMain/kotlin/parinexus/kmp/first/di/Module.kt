package parinexus.kmp.first.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import parinexus.kmp.first.core.database.di.databaseModule
import parinexus.kmp.first.core.network.di.networkModule
import parinexus.kmp.first.feature.coins.di.coinsModule
import parinexus.kmp.first.feature.portfolio.di.portfolioModule
import parinexus.kmp.first.feature.trade.di.tradeModule

fun initKoin(config: KoinAppDeclaration? = null) =
    startKoin {
        config?.invoke(this)
        modules(
            networkModule,
            databaseModule,
            coinsModule,
            portfolioModule,
            tradeModule,
            appModule,
            platformModule,
        )
    }

expect val platformModule: Module
