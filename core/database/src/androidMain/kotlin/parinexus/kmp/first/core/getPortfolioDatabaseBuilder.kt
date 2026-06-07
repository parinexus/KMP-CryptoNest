package parinexus.kmp.first.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import parinexus.kmp.first.core.database.portfolio.PortfolioDatabase

fun getPortfolioDatabaseBuilder(context: Context): RoomDatabase.Builder<PortfolioDatabase> {
    val appContext = context.applicationContext
    val dbPath = appContext.getDatabasePath("portfolio.db").absolutePath
    return Room.databaseBuilder<PortfolioDatabase>(
        context = appContext,
        name = dbPath,
    )
}
