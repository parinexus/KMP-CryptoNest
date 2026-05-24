package parinexus.kmp.first.core.api.data.mapper

import parinexus.kmp.first.core.api.data.parser.ApiErrorResponseParser
import parinexus.kmp.first.core.api.data.parser.CoinrankingApiErrorResponseParser
import parinexus.kmp.first.core.api.domain.RemoteFailure
import parinexus.kmp.first.core.domain.DataError

/**
 * Maps HTTP status + body to a domain [RemoteFailure].
 */
interface RemoteFailureMapper {
    fun map(httpStatus: Int, body: String): RemoteFailure
}

class CoinrankingRemoteFailureMapper(
    private val errorParser: ApiErrorResponseParser = CoinrankingApiErrorResponseParser(),
) : RemoteFailureMapper {

    override fun map(httpStatus: Int, body: String): RemoteFailure {
        val apiError = errorParser.parse(body)
        val apiMessage = apiError?.message?.takeIf { it.isNotBlank() }

        val errorType = when {
            apiError?.code == "RATE_LIMIT_EXCEEDED" || httpStatus == 429 ->
                DataError.Remote.TOO_MANY_REQUESTS
            httpStatus == 408 -> DataError.Remote.REQUEST_TIMEOUT
            httpStatus in 500..599 -> DataError.Remote.SERVER
            httpStatus == 401 || httpStatus == 403 -> DataError.Remote.UNKNOWN
            apiError != null -> DataError.Remote.UNKNOWN
            else -> DataError.Remote.UNKNOWN
        }

        return RemoteFailure(
            error = errorType,
            apiMessage = apiMessage,
            apiCode = apiError?.code,
            httpStatus = httpStatus,
        )
    }
}
