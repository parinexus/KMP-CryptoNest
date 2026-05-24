package parinexus.kmp.first.core.api.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinrankingErrorResponseDto(
    val status: String,
    val code: String? = null,
    val message: String? = null,
)
