package parinexus.kmp.first.core.database.portfolio

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import parinexus.kmp.first.core.domain.InsufficientFundsException
import parinexus.kmp.first.portfolio.data.local.PortfolioCoinEntity
import parinexus.kmp.first.portfolio.data.local.UserBalanceEntity
import parinexus.kmp.first.trade.data.local.TradeRecordEntity

@RunWith(AndroidJUnit4::class)
class PortfolioTransactionDaoTest {

    private lateinit var database: PortfolioDatabase
    private lateinit var transactionDao: PortfolioTransactionDao

    @Before
    fun setUp() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, PortfolioDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        transactionDao = database.portfolioTransactionDao()
        database.userBalanceDao().insertBalance(UserBalanceEntity(cashBalance = 1_000.0))
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun executeBuyWithTrade_deductsCashAndPersistsHolding() = runTest {
        transactionDao.executeBuyWithTrade(
            portfolioUpdate = bitcoinEntity(amountOwned = 0.01),
            amountInFiat = 500.0,
            tradeRecord = buyTradeRecord(),
        )

        assertEquals(500.0, database.userBalanceDao().getCashBalance())
        assertEquals(0.01, database.portfolioDao().getCoinById(BITCOIN_ID)?.amountOwned)
        assertEquals(1, database.tradeHistoryDao().observeAll().first().size)
    }

    @Test
    fun executeBuyWithTrade_failsWhenInsufficientFunds() = runTest {
        assertFailsWith<InsufficientFundsException> {
            transactionDao.executeBuyWithTrade(
                portfolioUpdate = bitcoinEntity(amountOwned = 0.02),
                amountInFiat = 1_500.0,
                tradeRecord = buyTradeRecord(),
            )
        }

        assertEquals(1_000.0, database.userBalanceDao().getCashBalance())
        assertEquals(null, database.portfolioDao().getCoinById(BITCOIN_ID))
        assertEquals(0, database.tradeHistoryDao().observeAll().first().size)
    }

    @Test
    fun executeSellWithTrade_addsCashAndRemovesHolding() = runTest {
        database.portfolioDao().insert(bitcoinEntity(amountOwned = 0.5))
        transactionDao.executeSellWithTrade(
            portfolioUpdate = null,
            removeCoinId = BITCOIN_ID,
            amountInFiat = 750.0,
            tradeRecord = sellTradeRecord(),
        )

        assertEquals(1_750.0, database.userBalanceDao().getCashBalance())
        assertEquals(null, database.portfolioDao().getCoinById(BITCOIN_ID))
        assertEquals(1, database.tradeHistoryDao().observeAll().first().size)
    }

    private fun bitcoinEntity(amountOwned: Double) = PortfolioCoinEntity(
        coinId = BITCOIN_ID,
        name = "Bitcoin",
        symbol = "BTC",
        iconUrl = "https://example.com/btc.png",
        averagePurchasePrice = 50_000.0,
        amountOwned = amountOwned,
        timestamp = 1L,
    )

    private fun buyTradeRecord() = TradeRecordEntity(
        coinId = BITCOIN_ID,
        coinName = "Bitcoin",
        coinSymbol = "BTC",
        coinIconUrl = "https://example.com/btc.png",
        type = "BUY",
        amountInFiat = 500.0,
        amountInUnit = 0.01,
        priceAtTrade = 50_000.0,
        executedAtEpochMs = 1L,
    )

    private fun sellTradeRecord() = TradeRecordEntity(
        coinId = BITCOIN_ID,
        coinName = "Bitcoin",
        coinSymbol = "BTC",
        coinIconUrl = "https://example.com/btc.png",
        type = "SELL",
        amountInFiat = 750.0,
        amountInUnit = 0.5,
        priceAtTrade = 1_500.0,
        executedAtEpochMs = 2L,
    )

    private companion object {
        const val BITCOIN_ID = "bitcoin-id"
    }
}
