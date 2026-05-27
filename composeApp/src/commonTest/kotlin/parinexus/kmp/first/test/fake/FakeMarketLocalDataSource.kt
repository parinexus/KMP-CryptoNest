package parinexus.kmp.first.test.fake

import parinexus.kmp.first.coins.data.local.MarketLocalDataSource
import parinexus.kmp.first.coins.data.remote.dto.CoinDetailsResponseDto
import parinexus.kmp.first.coins.data.remote.dto.CoinPriceHistoryResponseDto
import parinexus.kmp.first.coins.domain.model.CoinDetailModel
import parinexus.kmp.first.coins.domain.model.CoinInfoModel
import parinexus.kmp.first.coins.domain.model.PriceModel
import parinexus.kmp.first.coins.data.mapper.toCoinDetailModel
import parinexus.kmp.first.coins.data.mapper.toPriceModel

class FakeMarketLocalDataSource : MarketLocalDataSource {

    var coins: List<CoinInfoModel>? = null
    var coinsCachedAt: Long? = null

    private val details = mutableMapOf<String, CoinDetailsResponseDto>()
    private val detailCachedAt = mutableMapOf<String, Long>()
    private val priceHistory = mutableMapOf<String, CoinPriceHistoryResponseDto>()
    private val priceHistoryCachedAt = mutableMapOf<String, Long>()

    override suspend fun getCachedCoinsList(): List<CoinInfoModel>? = coins

    override suspend fun getCoinsListCachedAt(): Long? = coinsCachedAt

    override suspend fun saveCoinsList(coins: List<CoinInfoModel>, cachedAtEpochMs: Long) {
        this.coins = coins
        this.coinsCachedAt = cachedAtEpochMs
    }

    override suspend fun getCachedCoinDetail(coinId: String): CoinDetailModel? =
        details[coinId]?.data?.coin?.toCoinDetailModel()

    override suspend fun getCoinDetailCachedAt(coinId: String): Long? = detailCachedAt[coinId]

    override suspend fun saveCoinDetail(
        coinId: String,
        dto: CoinDetailsResponseDto,
        cachedAtEpochMs: Long,
    ) {
        details[coinId] = dto
        detailCachedAt[coinId] = cachedAtEpochMs
    }

    override suspend fun getCachedPriceHistory(coinId: String): List<PriceModel>? =
        priceHistory[coinId]?.data?.history?.map { it.toPriceModel() }

    override suspend fun getPriceHistoryCachedAt(coinId: String): Long? =
        priceHistoryCachedAt[coinId]

    override suspend fun savePriceHistory(
        coinId: String,
        dto: CoinPriceHistoryResponseDto,
        cachedAtEpochMs: Long,
    ) {
        priceHistory[coinId] = dto
        priceHistoryCachedAt[coinId] = cachedAtEpochMs
    }
}
