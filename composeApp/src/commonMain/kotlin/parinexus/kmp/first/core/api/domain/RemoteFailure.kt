package parinexus.kmp.first.core.api.domain

import parinexus.kmp.first.core.domain.DataError

/**
 * Normalized remote failure from an HTTP/API response.
 */
data class RemoteFailure(
    val error: DataError.Remote,
    val apiMessage: String? = null,
    val apiCode: String? = null,
    val httpStatus: Int? = null,
)
