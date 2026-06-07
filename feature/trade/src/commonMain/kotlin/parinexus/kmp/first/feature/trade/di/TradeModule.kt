package parinexus.kmp.first.feature.trade.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import parinexus.kmp.first.trade.data.repository.TradeHistoryRepositoryImpl
import parinexus.kmp.first.trade.data.TradePortfolioWriterImpl
import parinexus.kmp.first.trade.domain.TradePortfolioWriter
import parinexus.kmp.first.trade.domain.BuyCoinUseCase
import parinexus.kmp.first.trade.domain.ObserveTradeHistoryUseCase
import parinexus.kmp.first.trade.domain.RecordTradeUseCase
import parinexus.kmp.first.trade.domain.SellCoinUseCase
import parinexus.kmp.first.trade.domain.repository.TradeHistoryRepository
import parinexus.kmp.first.trade.presentation.buy.BuyViewModel
import parinexus.kmp.first.trade.presentation.history.TradeHistoryViewModel
import parinexus.kmp.first.trade.presentation.sell.SellViewModel

val tradeModule: Module = module {
    singleOf(::TradeHistoryRepositoryImpl).bind<TradeHistoryRepository>()
    singleOf(::TradePortfolioWriterImpl).bind<TradePortfolioWriter>()
    singleOf(::RecordTradeUseCase)
    singleOf(::ObserveTradeHistoryUseCase)
    singleOf(::BuyCoinUseCase)
    singleOf(::SellCoinUseCase)
    viewModel { TradeHistoryViewModel(get()) }
    viewModel { (coinId: String) -> BuyViewModel(get(), get(), get(), coinId) }
    viewModel { (coinId: String) -> SellViewModel(get(), get(), get(), coinId) }
}
