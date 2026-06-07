package parinexus.kmp.first.feature.portfolio.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import parinexus.kmp.first.portfolio.data.PortfolioRepositoryImpl
import parinexus.kmp.first.portfolio.domain.PortfolioRepository
import parinexus.kmp.first.portfolio.presentation.PortfolioViewModel

val portfolioModule: Module = module {
    singleOf(::PortfolioRepositoryImpl).bind<PortfolioRepository>()
    viewModel { PortfolioViewModel(get()) }
}
