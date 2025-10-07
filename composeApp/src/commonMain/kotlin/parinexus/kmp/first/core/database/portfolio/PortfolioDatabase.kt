package parinexus.kmp.first.core.database.portfolio

import androidx.room.Database
import androidx.room.RoomDatabase
import parinexus.kmp.first.portfolio.data.local.PortfolioCoinEntity
import parinexus.kmp.first.portfolio.data.local.PortfolioDao

@Database(entities = [PortfolioCoinEntity::class], version = 1)
abstract class PortfolioDatabase: RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
}