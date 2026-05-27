package parinexus.kmp.first.di

import androidx.room.RoomDatabase
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import parinexus.kmp.first.coins.data.local.MarketLocalDataSource
import parinexus.kmp.first.coins.data.local.MarketLocalDataSourceImpl
import parinexus.kmp.first.coins.data.remote.impl.CoinsRemoteDataSourceImpl
import parinexus.kmp.first.coins.data.repository.CoinsRepositoryImpl
import parinexus.kmp.first.coins.domain.FetchCoinDetailsUseCase
import parinexus.kmp.first.coins.domain.FetchCoinPriceHistoryUseCase
import parinexus.kmp.first.coins.domain.FetchCoinsListUseCase
import parinexus.kmp.first.coins.domain.api.CoinsRemoteDataSource
import parinexus.kmp.first.coins.domain.repository.CoinsRepository
import parinexus.kmp.first.coins.presentation.CoinsListViewModel
import parinexus.kmp.first.coins.presentation.detail.CoinDetailViewModel
import parinexus.kmp.first.core.database.portfolio.PortfolioDatabase
import parinexus.kmp.first.core.database.portfolio.getPortfolioDatabase
import parinexus.kmp.first.core.network.HttpClientFactory
import parinexus.kmp.first.core.serialization.AppJson
import parinexus.kmp.first.portfolio.data.PortfolioRepositoryImpl
import parinexus.kmp.first.portfolio.domain.PortfolioRepository
import parinexus.kmp.first.portfolio.presentation.PortfolioViewModel
import parinexus.kmp.first.trade.domain.BuyCoinUseCase
import parinexus.kmp.first.trade.domain.SellCoinUseCase
import parinexus.kmp.first.trade.presentation.buy.BuyViewModel
import parinexus.kmp.first.trade.presentation.sell.SellViewModel

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
    single<Json> { AppJson.instance }
    single<HttpClient> { HttpClientFactory.create(get()) }

    // portfolio + market cache (single Room database)
    single {
        getPortfolioDatabase(get<RoomDatabase.Builder<PortfolioDatabase>>())
    }
    single { get<PortfolioDatabase>().portfolioDao() }
    single { get<PortfolioDatabase>().userBalanceDao() }
    single { get<PortfolioDatabase>().marketCacheDao() }
    singleOf(::MarketLocalDataSourceImpl).bind<MarketLocalDataSource>()
    singleOf(::CoinsRemoteDataSourceImpl).bind<CoinsRemoteDataSource>()
    singleOf(::CoinsRepositoryImpl).bind<CoinsRepository>()
    singleOf(::PortfolioRepositoryImpl).bind<PortfolioRepository>()
    viewModel { PortfolioViewModel(get()) }

    // coins
    singleOf(::FetchCoinsListUseCase)
    singleOf(::FetchCoinDetailsUseCase)
    singleOf(::FetchCoinPriceHistoryUseCase)
    viewModel { CoinsListViewModel(get(), get()) }
    viewModel { (coinId: String) -> CoinDetailViewModel(get(), get(), coinId) }

    // trade
    singleOf(::BuyCoinUseCase)
    singleOf(::SellCoinUseCase)
    viewModel { (coinId: String) -> BuyViewModel(get(), get(), get(), coinId) }
    viewModel { (coinId: String) -> SellViewModel(get(), get(), get(), coinId) }
}
