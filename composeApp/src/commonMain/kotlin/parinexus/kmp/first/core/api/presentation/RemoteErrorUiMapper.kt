package parinexus.kmp.first.core.api.presentation

import parinexus.kmp.first.core.api.domain.RemoteFailure
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result

/**
 * Maps remote failures to user-visible messages (API message first, then fallback).
 */
object RemoteErrorUiMapper {

    fun toDisplayMessage(error: Result.Error<DataError.Remote>): String =
        error.message?.takeIf { it.isNotBlank() } ?: fallbackFor(error.error)

    fun toDisplayMessage(failure: RemoteFailure): String =
        failure.apiMessage?.takeIf { it.isNotBlank() } ?: fallbackFor(failure.error)

    fun fallbackFor(error: DataError.Remote): String = when (error) {
        DataError.Remote.TOO_MANY_REQUESTS ->
            "Too many API requests. Wait a moment or add your Coinranking API key."
        DataError.Remote.REQUEST_TIMEOUT ->
            "Request timed out. Check your connection and try again."
        DataError.Remote.NO_INTERNET ->
            "No internet connection."
        DataError.Remote.SERVER ->
            "Server error. Please try again later."
        DataError.Remote.SERIALIZATION ->
            "Could not read data from the server."
        DataError.Remote.UNKNOWN ->
            "Something went wrong. Please try again."
    }
}
