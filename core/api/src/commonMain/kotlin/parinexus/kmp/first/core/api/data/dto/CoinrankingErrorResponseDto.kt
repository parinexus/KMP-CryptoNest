package parinexus.kmp.first.core.api.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinrankingErrorResponseDto(
    val status: String,
    /** Legacy / alternate field; Coinranking docs often use [type] for error codes. */
    val code: String? = null,
    val type: String? = null,
    val message: String? = null,
) {
    val errorCode: String?
        get() = type?.takeIf { it.isNotBlank() } ?: code?.takeIf { it.isNotBlank() }
}
