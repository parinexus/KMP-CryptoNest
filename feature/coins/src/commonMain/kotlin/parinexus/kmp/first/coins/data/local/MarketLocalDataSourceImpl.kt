package parinexus.kmp.first.coins.data.local

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import parinexus.kmp.first.coins.data.mapper.toCachedCoinEntity
import parinexus.kmp.first.coins.data.mapper.toCoinDetailModel
import parinexus.kmp.first.coins.data.mapper.toCoinInfoModel
import parinexus.kmp.first.coins.data.mapper.toPriceModel
import parinexus.kmp.first.coins.data.remote.dto.CoinDetailsResponseDto
import parinexus.kmp.first.coins.data.remote.dto.CoinPriceHistoryResponseDto
import parinexus.kmp.first.coins.domain.model.CoinDetailModel
import parinexus.kmp.first.coins.domain.model.CoinInfoModel
import parinexus.kmp.first.coins.domain.model.PriceModel
import parinexus.kmp.first.core.domain.cache.MarketCachePolicy

class MarketLocalDataSourceImpl(
    private val dao: MarketCacheDao,
    private val json: Json,
) : MarketLocalDataSource {

    override suspend fun getCachedCoinsList(): List<CoinInfoModel>? {
        val entities = dao.getCachedCoins()
        if (entities.isEmpty()) return null
        return entities.map { it.toCoinInfoModel() }
    }

    override suspend fun getCoinsListCachedAt(): Long? =
        dao.getMeta(MarketCachePolicy.COINS_LIST_KEY)?.cachedAtEpochMs

    override suspend fun saveCoinsList(coins: List<CoinInfoModel>, cachedAtEpochMs: Long) {
        val entities = coins.mapIndexed { index, coin -> coin.toCachedCoinEntity(index) }
        dao.replaceCoinsList(
            coins = entities,
            meta = MarketCacheMetaEntity(
                cacheKey = MarketCachePolicy.COINS_LIST_KEY,
                cachedAtEpochMs = cachedAtEpochMs,
            ),
        )
    }

    override suspend fun getCachedCoinDetail(coinId: String): CoinDetailModel? {
        val cached = dao.getCoinDetail(coinId) ?: return null
        return runCatching {
            json.decodeFromString<CoinDetailsResponseDto>(cached.responseJson)
                .data.coin.toCoinDetailModel()
        }.getOrNull()
    }

    override suspend fun getCoinDetailCachedAt(coinId: String): Long? =
        dao.getCoinDetail(coinId)?.cachedAtEpochMs

    override suspend fun saveCoinDetail(
        coinId: String,
        dto: CoinDetailsResponseDto,
        cachedAtEpochMs: Long,
    ) {
        dao.upsertCoinDetail(
            CachedCoinDetailEntity(
                coinId = coinId,
                responseJson = json.encodeToString(dto),
                cachedAtEpochMs = cachedAtEpochMs,
            ),
        )
        dao.upsertMeta(
            MarketCacheMetaEntity(
                cacheKey = MarketCachePolicy.coinDetailKey(coinId),
                cachedAtEpochMs = cachedAtEpochMs,
            ),
        )
    }

    override suspend fun getCachedPriceHistory(coinId: String): List<PriceModel>? {
        val cached = dao.getPriceHistory(coinId) ?: return null
        return runCatching {
            json.decodeFromString<CoinPriceHistoryResponseDto>(cached.responseJson)
                .data.history.map { it.toPriceModel() }
        }.getOrNull()
    }

    override suspend fun getPriceHistoryCachedAt(coinId: String): Long? =
        dao.getPriceHistory(coinId)?.cachedAtEpochMs

    override suspend fun savePriceHistory(
        coinId: String,
        dto: CoinPriceHistoryResponseDto,
        cachedAtEpochMs: Long,
    ) {
        dao.upsertPriceHistory(
            CachedPriceHistoryEntity(
                coinId = coinId,
                responseJson = json.encodeToString(dto),
                cachedAtEpochMs = cachedAtEpochMs,
            ),
        )
        dao.upsertMeta(
            MarketCacheMetaEntity(
                cacheKey = MarketCachePolicy.priceHistoryKey(coinId),
                cachedAtEpochMs = cachedAtEpochMs,
            ),
        )
    }
}

