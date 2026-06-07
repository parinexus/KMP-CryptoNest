package parinexus.kmp.first.core.database.di

import androidx.room.RoomDatabase
import org.koin.core.module.Module
import org.koin.dsl.module
import parinexus.kmp.first.core.database.portfolio.PortfolioDatabase
import parinexus.kmp.first.core.database.portfolio.getPortfolioDatabase

val databaseModule: Module = module {
    single {
        getPortfolioDatabase(get<RoomDatabase.Builder<PortfolioDatabase>>())
    }
    single { get<PortfolioDatabase>().portfolioDao() }
    single { get<PortfolioDatabase>().userBalanceDao() }
    single { get<PortfolioDatabase>().marketCacheDao() }
    single { get<PortfolioDatabase>().tradeHistoryDao() }
    single { get<PortfolioDatabase>().portfolioTransactionDao() }
}
