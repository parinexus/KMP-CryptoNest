package parinexus.kmp.first.core.database.portfolio

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import parinexus.kmp.first.portfolio.data.local.PortfolioCoinEntity
import parinexus.kmp.first.portfolio.data.local.PortfolioDao
import parinexus.kmp.first.portfolio.data.local.UserBalanceDao
import parinexus.kmp.first.portfolio.data.local.UserBalanceEntity

@ConstructedBy(PortfolioDatabaseCreator::class)
@Database(entities = [PortfolioCoinEntity::class, UserBalanceEntity::class], version = 3)
abstract class PortfolioDatabase: RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
    abstract fun userBalanceDao(): UserBalanceDao
}