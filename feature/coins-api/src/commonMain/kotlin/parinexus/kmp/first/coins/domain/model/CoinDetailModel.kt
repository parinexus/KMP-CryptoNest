package parinexus.kmp.first.coins.domain.model

import parinexus.kmp.first.core.domain.coin.Coin

data class CoinDetailModel(
    val coin: Coin,
    val description: String,
    val price: Double,
    val changePercent: Double,
    val rank: Int,
    val marketCap: Double?,
    val volume24h: Double?,
    val sparkline: List<Double>,
    val circulatingSupply: Double?,
    val maxSupply: Double?,
    val allTimeHighPrice: Double?,
    val numberOfMarkets: Int?,
    val numberOfExchanges: Int?,
    val tags: List<String>,
    val notices: List<CoinNoticeModel>,
    val websiteUrl: String?,
)
