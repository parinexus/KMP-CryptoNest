package parinexus.kmp.first.core.api.data.parser

import kotlinx.serialization.json.Json
import parinexus.kmp.first.core.api.data.dto.CoinrankingErrorResponseDto

/**
 * Parses provider-specific API error payloads (Coinranking: status = "fail").
 */
interface ApiErrorResponseParser {
    fun parse(body: String): CoinrankingErrorResponseDto?
}

class CoinrankingApiErrorResponseParser(
    private val json: Json = ApiErrorJson,
) : ApiErrorResponseParser {

    override fun parse(body: String): CoinrankingErrorResponseDto? {
        return runCatching {
            json.decodeFromString<CoinrankingErrorResponseDto>(body)
        }.getOrNull()?.takeIf { it.status.equals("fail", ignoreCase = true) }
    }
}

internal val ApiErrorJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}
