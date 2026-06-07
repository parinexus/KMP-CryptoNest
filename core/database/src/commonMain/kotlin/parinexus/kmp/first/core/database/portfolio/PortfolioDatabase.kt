package parinexus.kmp.first.core.database.portfolio

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import parinexus.kmp.first.coins.data.local.CachedCoinDetailEntity
import parinexus.kmp.first.coins.data.local.CachedCoinEntity
import parinexus.kmp.first.coins.data.local.CachedPriceHistoryEntity
import parinexus.kmp.first.coins.data.local.MarketCacheDao
import parinexus.kmp.first.coins.data.local.MarketCacheMetaEntity
import parinexus.kmp.first.portfolio.data.local.PortfolioCoinEntity
import parinexus.kmp.first.portfolio.data.local.PortfolioDao
import parinexus.kmp.first.portfolio.data.local.UserBalanceDao
import parinexus.kmp.first.portfolio.data.local.UserBalanceEntity
import parinexus.kmp.first.trade.data.local.TradeRecordEntity
import parinexus.kmp.first.trade.data.local.TradeHistoryDao

@ConstructedBy(PortfolioDatabaseCreator::class)
@Database(
    entities = [
        PortfolioCoinEntity::class,
        UserBalanceEntity::class,
        CachedCoinEntity::class,
        MarketCacheMetaEntity::class,
        CachedCoinDetailEntity::class,
        CachedPriceHistoryEntity::class,
        TradeRecordEntity::class,
    ],
    version = 5,
)
abstract class PortfolioDatabase : RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
    abstract fun userBalanceDao(): UserBalanceDao
    abstract fun marketCacheDao(): MarketCacheDao
    abstract fun tradeHistoryDao(): TradeHistoryDao
    abstract fun portfolioTransactionDao(): PortfolioTransactionDao
}