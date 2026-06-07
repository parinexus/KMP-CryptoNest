package parinexus.kmp.first.portfolio.data

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import kotlin.test.Test
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import parinexus.kmp.first.coins.domain.repository.CoinsRepository
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.core.domain.cache.CachedData
import parinexus.kmp.first.coins.domain.model.CoinDetailModel
import parinexus.kmp.first.coins.domain.model.CoinInfoModel
import parinexus.kmp.first.coins.domain.model.PriceModel
import parinexus.kmp.first.portfolio.data.local.PortfolioCoinEntity
import parinexus.kmp.first.portfolio.data.local.PortfolioDao
import parinexus.kmp.first.portfolio.data.local.UserBalanceDao
import parinexus.kmp.first.portfolio.data.local.UserBalanceEntity
import parinexus.kmp.first.test.fixture.TestCoins

class PortfolioRepositoryImplTest {

    @Test
    fun observePortfolioSnapshot_doesNotResolvePricesWhenOnlyCashChanges() = runTest {
        val portfolioDao = FakePortfolioDao(
            listOf(
                PortfolioCoinEntity(
                    coinId = TestCoins.BITCOIN_ID,
                    name = TestCoins.bitcoin.name,
                    symbol = TestCoins.bitcoin.symbol,
                    iconUrl = TestCoins.bitcoin.iconUrl,
                    amountOwned = 0.5,
                    averagePurchasePrice = 48_000.0,
                    timestamp = 1L,
                ),
            ),
        )
        val userBalanceDao = FakeUserBalanceDao(initialBalance = 10_000.0)
        val trackingCoinsRepository = TrackingCoinsRepository(
            prices = mapOf(TestCoins.BITCOIN_ID to 50_000.0),
        )
        val repository = PortfolioRepositoryImpl(
            portfolioDao = portfolioDao,
            userBalanceDao = userBalanceDao,
            coinsRepository = trackingCoinsRepository,
        )

        repository.observePortfolioSnapshot().test {
            awaitItem()
            assertThat(trackingCoinsRepository.resolveMarketPricesInvocations).isEqualTo(1)

            userBalanceDao.setBalance(9_000.0)
            val cashOnlyUpdate = awaitItem()
            assertThat(cashOnlyUpdate).isInstanceOf(Result.Success::class)
            assertThat((cashOnlyUpdate as Result.Success).data.cashBalance).isEqualTo(9_000.0)
            assertThat(trackingCoinsRepository.resolveMarketPricesInvocations).isEqualTo(1)

            cancelAndIgnoreRemainingEvents()
        }
    }

    private class FakePortfolioDao(
        initialHoldings: List<PortfolioCoinEntity>,
    ) : PortfolioDao {
        private val holdings = MutableStateFlow(initialHoldings)

        override suspend fun insert(portfolioCoinEntity: PortfolioCoinEntity) {
            holdings.value = holdings.value.filterNot { it.coinId == portfolioCoinEntity.coinId } +
                portfolioCoinEntity
        }

        override fun getAllOwnedCoins(): Flow<List<PortfolioCoinEntity>> = holdings

        override suspend fun getCoinById(coinId: String): PortfolioCoinEntity? =
            holdings.value.firstOrNull { it.coinId == coinId }

        override suspend fun deletePortfolioItem(coinId: String) {
            holdings.value = holdings.value.filterNot { it.coinId == coinId }
        }
    }

    private class FakeUserBalanceDao(
        initialBalance: Double?,
    ) : UserBalanceDao {
        private val balance = MutableStateFlow(initialBalance)

        fun setBalance(value: Double) {
            balance.value = value
        }

        override suspend fun getCashBalance(): Double? = balance.value

        override fun observeCashBalance(): Flow<Double?> = balance

        override suspend fun insertBalance(userBalanceEntity: UserBalanceEntity) {
            balance.value = userBalanceEntity.cashBalance
        }

        override suspend fun updateCashBalance(newBalance: Double) {
            balance.value = newBalance
        }
    }

    private class TrackingCoinsRepository(
        private val prices: Map<String, Double>,
    ) : CoinsRepository {
        var resolveMarketPricesInvocations = 0

        override fun observeCoinsList(forceRefresh: Boolean): Flow<Result<CachedData<List<CoinInfoModel>>, DataError.Remote>> {
            error("Not used in portfolio tests")
        }

        override fun observeCoinDetail(
            coinId: String,
            forceRefresh: Boolean,
        ): Flow<Result<CachedData<CoinDetailModel>, DataError.Remote>> {
            error("Not used in portfolio tests")
        }

        override suspend fun getPriceHistory(
            coinId: String,
            forceRefresh: Boolean,
        ): Result<CachedData<List<PriceModel>>, DataError.Remote> {
            error("Not used in portfolio tests")
        }

        override suspend fun getCachedPricesByCoinId(): Map<String, Double> = prices

        override suspend fun resolveMarketPrices(
            coinIds: Collection<String>,
            forceRefresh: Boolean,
        ): Result<Map<String, Double>, DataError.Remote> {
            resolveMarketPricesInvocations++
            return Result.Success(prices.filterKeys { it in coinIds })
        }
    }
}
