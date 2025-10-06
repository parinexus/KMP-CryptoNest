package parinexus.kmp.first.di

import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import parinexus.kmp.first.coins.data.remote.impl.CoinsRemoteDataSourceImpl
import parinexus.kmp.first.coins.domain.FetchCoinDetailsUseCase
import parinexus.kmp.first.coins.domain.FetchCoinPriceHistoryUseCase
import parinexus.kmp.first.coins.domain.FetchCoinsListUseCase
import parinexus.kmp.first.coins.domain.api.CoinsRemoteDataSource
import parinexus.kmp.first.coins.presentation.CoinsListViewModel
import parinexus.kmp.first.core.network.HttpClientFactory

fun initKoin(config: KoinAppDeclaration? = null) =
    startKoin {
        config?.invoke(this)
        modules(
            sharedModule,
            platformModule,
        )
    }


expect val platformModule: Module

val sharedModule = module {

    // core
    single<HttpClient> { HttpClientFactory.create(get()) }
    viewModel { CoinsListViewModel(get(), get()) }
    singleOf(::FetchCoinsListUseCase)
    singleOf(::FetchCoinDetailsUseCase)
    singleOf(::FetchCoinPriceHistoryUseCase)
    singleOf(::CoinsRemoteDataSourceImpl).bind<CoinsRemoteDataSource>()
}