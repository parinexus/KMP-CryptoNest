package parinexus.kmp.first.coins.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinPriceHistoryResponseDto(
    val data: CoinPriceHistoryDto
)

@Serializable
data class CoinPriceHistoryDto(
    val history: List<CoinPriceDto>
)

@Serializable
data class CoinPriceDto(
    /** Coinranking returns price as a string. */
    val price: String?,
    val timestamp: Long,
)