package parinexus.kmp.first.coins.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinsResponseDto(
    val data: CoinsListDto
)

@Serializable
data class CoinsListDto(
    val coins: List<CoinItemDto>
)

@Serializable
data class CoinItemDto(
    val uuid: String,
    val symbol: String,
    val name: String,
    val iconUrl: String,
    /** Coinranking API returns price as a string (e.g. "76594.74"). */
    val price: String,
    val rank: Int,
    /** 24h change percent as a string (e.g. "1.54"). */
    val change: String,
)

fun CoinItemDto.priceAsDouble(): Double = price.toDoubleOrNull() ?: 0.0