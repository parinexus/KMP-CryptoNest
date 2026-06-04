package parinexus.kmp.first.coins.data.local

import parinexus.kmp.first.coins.data.remote.dto.CoinDetailsResponseDto
import parinexus.kmp.first.coins.data.remote.dto.CoinPriceHistoryResponseDto
import parinexus.kmp.first.coins.domain.model.CoinDetailModel
import parinexus.kmp.first.coins.domain.model.CoinInfoModel
import parinexus.kmp.first.coins.domain.model.PriceModel

interface MarketLocalDataSource {

    suspend fun getCachedCoinsList(): List<CoinInfoModel>?

    suspend fun getCoinsListCachedAt(): Long?

    suspend fun saveCoinsList(coins: List<CoinInfoModel>, cachedAtEpochMs: Long)

    suspend fun getCachedCoinDetail(coinId: String): CoinDetailModel?

    suspend fun getCoinDetailCachedAt(coinId: String): Long?

    suspend fun saveCoinDetail(coinId: String, dto: CoinDetailsResponseDto, cachedAtEpochMs: Long)

    suspend fun getCachedPriceHistory(coinId: String): List<PriceModel>?

    suspend fun getPriceHistoryCachedAt(coinId: String): Long?

    suspend fun savePriceHistory(
        coinId: String,
        dto: CoinPriceHistoryResponseDto,
        cachedAtEpochMs: Long,
    )
}
