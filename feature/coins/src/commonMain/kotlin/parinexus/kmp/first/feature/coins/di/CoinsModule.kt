package parinexus.kmp.first.feature.coins.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import parinexus.kmp.first.coins.data.local.MarketLocalDataSource
import parinexus.kmp.first.coins.data.local.MarketLocalDataSourceImpl
import parinexus.kmp.first.coins.data.remote.impl.CoinsRemoteDataSourceImpl
import parinexus.kmp.first.coins.data.repository.CoinsRepositoryImpl
import parinexus.kmp.first.coins.domain.FetchCoinDetailsUseCase
import parinexus.kmp.first.coins.domain.FetchCoinPriceHistoryUseCase
import parinexus.kmp.first.coins.domain.FetchCoinsListUseCase
import parinexus.kmp.first.coins.data.remote.CoinsRemoteDataSource
import parinexus.kmp.first.coins.domain.repository.CoinsRepository
import parinexus.kmp.first.coins.presentation.CoinsListViewModel
import parinexus.kmp.first.coins.presentation.detail.CoinDetailViewModel

val coinsModule: Module = module {
    singleOf(::MarketLocalDataSourceImpl).bind<MarketLocalDataSource>()
    singleOf(::CoinsRemoteDataSourceImpl).bind<CoinsRemoteDataSource>()
    singleOf(::CoinsRepositoryImpl).bind<CoinsRepository>()
    singleOf(::FetchCoinsListUseCase)
    singleOf(::FetchCoinDetailsUseCase)
    singleOf(::FetchCoinPriceHistoryUseCase)
    viewModel { CoinsListViewModel(get(), get()) }
    viewModel { (coinId: String) -> CoinDetailViewModel(get(), get(), coinId) }
}
