package parinexus.kmp.first.coins.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import parinexus.kmp.first.coins.data.local.MarketLocalDataSource
import parinexus.kmp.first.coins.data.mapper.toCoinDetailModel
import parinexus.kmp.first.coins.data.mapper.toCoinInfoModel
import parinexus.kmp.first.coins.data.mapper.toPriceModel
import parinexus.kmp.first.coins.data.remote.CoinsRemoteDataSource
import parinexus.kmp.first.coins.domain.model.CoinDetailModel
import parinexus.kmp.first.coins.domain.model.CoinInfoModel
import parinexus.kmp.first.coins.domain.model.PriceModel
import parinexus.kmp.first.coins.domain.repository.CoinsRepository
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.core.domain.cache.CachedData
import parinexus.kmp.first.core.domain.cache.DataFreshness
import parinexus.kmp.first.core.domain.cache.MarketCachePolicy
import parinexus.kmp.first.core.network.NetworkLogger
import parinexus.kmp.first.coins.data.remote.dto.priceAsDouble

class CoinsRepositoryImpl(
    private val remote: CoinsRemoteDataSource,
    private val local: MarketLocalDataSource,
) : CoinsRepository {

    override fun observeCoinsList(forceRefresh: Boolean): Flow<Result<CachedData<List<CoinInfoModel>>, DataError.Remote>> =
        flow {
            val cached = local.getCachedCoinsList()
            val cachedAt = local.getCoinsListCachedAt()

            if (cached != null && !forceRefresh) {
                emit(
                    Result.Success(
                        CachedData(
                            value = cached,
                            freshness = MarketCachePolicy.evaluateFreshness(
                                cachedAt,
                                MarketCachePolicy.MARKET_DATA_TTL_MS,
                            ),
                            cachedAtEpochMs = cachedAt,
                        ),
                    ),
                )
            }

            when (val remoteResult = remote.getListOfCoins()) {
                is Result.Success -> {
                    val now = nowMs()
                    val coins = remoteResult.data.data.coins.map { it.toCoinInfoModel() }
                    local.saveCoinsList(coins, now)
                    NetworkLogger.d("CoinsRepository: coins list refreshed (${coins.size} coins)")
                    emit(
                        Result.Success(
                            CachedData(
                                value = coins,
                                freshness = DataFreshness.Fresh,
                                cachedAtEpochMs = now,
                            ),
                        ),
                    )
                }
                is Result.Error -> {
                    if (cached != null) {
                        NetworkLogger.e("CoinsRepository: list remote failed, serving cache — ${remoteResult.error}")
                        emit(
                            Result.Success(
                                CachedData(
                                    value = cached,
                                    freshness = offlineFreshness(remoteResult.error, cachedAt),
                                    cachedAtEpochMs = cachedAt,
                                ),
                            ),
                        )
                    } else {
                        emit(remoteResult)
                    }
                }
            }
        }

    override fun observeCoinDetail(
        coinId: String,
        forceRefresh: Boolean,
    ): Flow<Result<CachedData<CoinDetailModel>, DataError.Remote>> = flow {
        val cached = local.getCachedCoinDetail(coinId)
        val cachedAt = local.getCoinDetailCachedAt(coinId)

        if (cached != null && !forceRefresh) {
            emit(
                Result.Success(
                    CachedData(
                        value = cached,
                        freshness = MarketCachePolicy.evaluateFreshness(
                            cachedAt,
                            MarketCachePolicy.MARKET_DATA_TTL_MS,
                        ),
                        cachedAtEpochMs = cachedAt,
                    ),
                ),
            )
        }

        when (val remoteResult = remote.getCoinById(coinId)) {
            is Result.Success -> {
                val now = nowMs()
                local.saveCoinDetail(coinId, remoteResult.data, now)
                val detail = remoteResult.data.data.coin.toCoinDetailModel()
                emit(
                    Result.Success(
                        CachedData(
                            value = detail,
                            freshness = DataFreshness.Fresh,
                            cachedAtEpochMs = now,
                        ),
                    ),
                )
            }
            is Result.Error -> {
                if (cached != null) {
                    emit(
                        Result.Success(
                            CachedData(
                                value = cached,
                                freshness = offlineFreshness(remoteResult.error, cachedAt),
                                cachedAtEpochMs = cachedAt,
                            ),
                        ),
                    )
                } else {
                    emit(remoteResult)
                }
            }
        }
    }

    override suspend fun getPriceHistory(
        coinId: String,
        forceRefresh: Boolean,
    ): Result<CachedData<List<PriceModel>>, DataError.Remote> {
        val cached = local.getCachedPriceHistory(coinId)
        val cachedAt = local.getPriceHistoryCachedAt(coinId)

        if (cached != null && !forceRefresh) {
            val freshness = MarketCachePolicy.evaluateFreshness(
                cachedAt,
                MarketCachePolicy.CHART_DATA_TTL_MS,
            )
            if (freshness == DataFreshness.Cached) {
                return Result.Success(
                    CachedData(
                        value = cached,
                        freshness = freshness,
                        cachedAtEpochMs = cachedAt,
                    ),
                )
            }
        }

        return when (val remoteResult = remote.getPriceHistory(coinId)) {
            is Result.Success -> {
                val now = nowMs()
                local.savePriceHistory(coinId, remoteResult.data, now)
                val points = remoteResult.data.data.history.map { it.toPriceModel() }
                Result.Success(
                    CachedData(
                        value = points,
                        freshness = DataFreshness.Fresh,
                        cachedAtEpochMs = now,
                    ),
                )
            }
            is Result.Error -> {
                if (cached != null) {
                    Result.Success(
                        CachedData(
                            value = cached,
                            freshness = offlineFreshness(remoteResult.error, cachedAt),
                            cachedAtEpochMs = cachedAt,
                        ),
                    )
                } else {
                    remoteResult
                }
            }
        }
    }

    override suspend fun getCachedPricesByCoinId(): Map<String, Double> =
        local.getCachedCoinsList()
            ?.associate { it.coin.id to it.price }
            ?: emptyMap()

    override suspend fun resolveMarketPrices(
        coinIds: Collection<String>,
        forceRefresh: Boolean,
    ): Result<Map<String, Double>, DataError.Remote> {
        if (coinIds.isEmpty()) return Result.Success(emptyMap())

        val uniqueIds = coinIds.toSet()
        val cached = local.getCachedCoinsList()
        val cachedAt = local.getCoinsListCachedAt()
        val cachedPrices = cached?.associate { it.coin.id to it.price } ?: emptyMap()
        val hasAllPrices = uniqueIds.all { cachedPrices.containsKey(it) }
        val isFresh = !forceRefresh &&
            cached != null &&
            hasAllPrices &&
            MarketCachePolicy.evaluateFreshness(cachedAt, MarketCachePolicy.MARKET_DATA_TTL_MS) != DataFreshness.Stale

        if (isFresh) {
            return Result.Success(uniqueIds.associateWith { cachedPrices[it] ?: 0.0 })
        }

        return when (val remoteResult = remote.getListOfCoins()) {
            is Result.Success -> {
                val now = nowMs()
                val coins = remoteResult.data.data.coins.map { it.toCoinInfoModel() }
                local.saveCoinsList(coins, now)
                NetworkLogger.d("CoinsRepository: market prices refreshed (${coins.size} coins)")
                val prices = coins.associate { it.coin.id to it.price }
                Result.Success(uniqueIds.associateWith { prices[it] ?: cachedPrices[it] ?: 0.0 })
            }
            is Result.Error -> {
                if (cachedPrices.isNotEmpty()) {
                    NetworkLogger.e("CoinsRepository: market prices remote failed, serving cache — ${remoteResult.error}")
                    Result.Success(uniqueIds.associateWith { cachedPrices[it] ?: 0.0 })
                } else {
                    Result.Error(remoteResult.error)
                }
            }
        }
    }

    private fun offlineFreshness(error: DataError.Remote, cachedAt: Long?): DataFreshness =
        when (error) {
            DataError.Remote.NO_INTERNET -> DataFreshness.Offline
            else -> MarketCachePolicy.evaluateFreshness(cachedAt, MarketCachePolicy.MARKET_DATA_TTL_MS)
                .let { if (it == DataFreshness.Cached) DataFreshness.Stale else it }
        }

    private fun nowMs(): Long = Clock.System.now().toEpochMilliseconds()
}
