package parinexus.kmp.first.core.database.portfolio

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

/**
 * v4 adds offline market cache tables; portfolio tables are unchanged from v3.
 */
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `cached_coins` (
                `id` TEXT NOT NULL,
                `name` TEXT NOT NULL,
                `symbol` TEXT NOT NULL,
                `iconUrl` TEXT NOT NULL,
                `price` REAL NOT NULL,
                `changePercent` REAL NOT NULL,
                `rank` INTEGER NOT NULL,
                `listOrder` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent(),
        )
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `market_cache_meta` (
                `cacheKey` TEXT NOT NULL,
                `cachedAtEpochMs` INTEGER NOT NULL,
                PRIMARY KEY(`cacheKey`)
            )
            """.trimIndent(),
        )
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `cached_coin_details` (
                `coinId` TEXT NOT NULL,
                `responseJson` TEXT NOT NULL,
                `cachedAtEpochMs` INTEGER NOT NULL,
                PRIMARY KEY(`coinId`)
            )
            """.trimIndent(),
        )
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `cached_price_history` (
                `coinId` TEXT NOT NULL,
                `responseJson` TEXT NOT NULL,
                `cachedAtEpochMs` INTEGER NOT NULL,
                PRIMARY KEY(`coinId`)
            )
            """.trimIndent(),
        )
    }
}
