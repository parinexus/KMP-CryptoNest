package parinexus.kmp.first.coins.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinDetailDto(
    val uuid: String,
    val symbol: String,
    val name: String,
    val description: String? = null,
    val color: String? = null,
    val iconUrl: String,
    val websiteUrl: String? = null,
    val price: String,
    val change: String,
    val rank: Int,
    val marketCap: String? = null,
    @SerialName("24hVolume")
    val volume24h: String? = null,
    val sparkline: List<String>? = null,
    val supply: CoinSupplyDto? = null,
    val allTimeHigh: CoinAllTimeHighDto? = null,
    val numberOfMarkets: Int? = null,
    val numberOfExchanges: Int? = null,
    val tags: List<String>? = null,
    val notices: List<CoinNoticeDto>? = null,
)

@Serializable
data class CoinSupplyDto(
    val confirmed: Boolean? = null,
    val circulating: String? = null,
    val total: String? = null,
    val max: String? = null,
)

@Serializable
data class CoinAllTimeHighDto(
    val price: String? = null,
    val timestamp: Long? = null,
)

@Serializable
data class CoinNoticeDto(
    val type: String? = null,
    val value: String? = null,
)

fun CoinDetailDto.priceAsDouble(): Double = price.toDoubleOrNull() ?: 0.0
