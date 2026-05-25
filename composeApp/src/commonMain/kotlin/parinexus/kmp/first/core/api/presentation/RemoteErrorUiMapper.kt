package parinexus.kmp.first.core.api.presentation

import parinexus.kmp.first.core.api.domain.RemoteFailure
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result

/**
 * Maps remote failures to user-visible messages (API message first, then fallback).
 * For rate limits, uses [context] so list vs detail vs chart errors are easy to tell apart.
 */
object RemoteErrorUiMapper {

    fun toDisplayMessage(
        error: Result.Error<DataError.Remote>,
        context: RemoteErrorContext,
    ): String = resolveMessage(
        error = error.error,
        apiMessage = error.message,
        context = context,
    )

    fun toDisplayMessage(
        failure: RemoteFailure,
        context: RemoteErrorContext,
    ): String = resolveMessage(
        error = failure.error,
        apiMessage = failure.apiMessage,
        context = context,
    )

    private fun resolveMessage(
        error: DataError.Remote,
        apiMessage: String?,
        context: RemoteErrorContext,
    ): String {
        val trimmedApi = apiMessage?.takeIf { it.isNotBlank() }
        return when (error) {
            DataError.Remote.TOO_MANY_REQUESTS -> rateLimitMessage(context)
            DataError.Remote.COIN_NOT_FOUND -> trimmedApi ?: coinNotFoundMessage(context)
            DataError.Remote.VALIDATION_ERROR -> trimmedApi ?: validationErrorMessage(context)
            DataError.Remote.REFERENCE_UNAVAILABLE -> trimmedApi ?: referenceUnavailableMessage(context)
            else -> trimmedApi ?: fallbackFor(error)
        }
    }

    private fun coinNotFoundMessage(context: RemoteErrorContext): String = when (context) {
        RemoteErrorContext.CoinDetail ->
            "Coin details: This coin was not found. Go back and pick another coin."
        RemoteErrorContext.CoinDetailChart,
        RemoteErrorContext.CoinPriceChartDialog,
        ->
            "Chart: This coin was not found."
        RemoteErrorContext.CoinsList ->
            "Coin list: A coin in the response could not be found."
    }

    private fun validationErrorMessage(context: RemoteErrorContext): String = when (context) {
        RemoteErrorContext.CoinDetail ->
            "Coin details: The request was invalid (check coin ID or parameters)."
        RemoteErrorContext.CoinDetailChart ->
            "24h chart (detail): The chart request was invalid."
        RemoteErrorContext.CoinPriceChartDialog ->
            "24h chart (popup): The chart request was invalid."
        RemoteErrorContext.CoinsList ->
            "Coin list: The request was invalid."
    }

    private fun referenceUnavailableMessage(context: RemoteErrorContext): String = when (context) {
        RemoteErrorContext.CoinDetail ->
            "Coin details: The reference currency is unavailable. Try again later."
        RemoteErrorContext.CoinDetailChart,
        RemoteErrorContext.CoinPriceChartDialog,
        ->
            "Chart: The reference currency is unavailable."
        RemoteErrorContext.CoinsList ->
            "Coin list: The reference currency is unavailable."
    }

    private fun rateLimitMessage(context: RemoteErrorContext): String = when (context) {
        RemoteErrorContext.CoinsList ->
            "Coin list: API rate limit reached. Wait a minute, then tap Retry on this screen."
        RemoteErrorContext.CoinDetail ->
            "Coin details: API rate limit reached. Wait a minute, then tap Retry on this screen."
        RemoteErrorContext.CoinDetailChart ->
            "24h chart (detail): API rate limit reached. Wait a minute, then tap Retry below the chart."
        RemoteErrorContext.CoinPriceChartDialog ->
            "24h chart (popup): API rate limit reached. Close the dialog and try again later."
    }

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
        DataError.Remote.COIN_NOT_FOUND ->
            "Coin not found."
        DataError.Remote.VALIDATION_ERROR ->
            "The request could not be validated. Please try again."
        DataError.Remote.REFERENCE_UNAVAILABLE ->
            "The reference currency is not available."
        DataError.Remote.UNKNOWN ->
            "Something went wrong. Please try again."
    }
}
