package parinexus.kmp.first.core.api.data.client

import parinexus.kmp.first.core.api.domain.RemoteFailure
import parinexus.kmp.first.core.api.data.mapper.CoinrankingRemoteFailureMapper
import parinexus.kmp.first.core.api.data.mapper.RemoteFailureMapper
import parinexus.kmp.first.core.api.data.parser.ApiErrorJson
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.core.network.NetworkLogger
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.json.Json
import kotlin.coroutines.coroutineContext

val apiResponseJson: Json = ApiErrorJson

class SafeApiClient(
    val failureMapper: RemoteFailureMapper = CoinrankingRemoteFailureMapper(),
) {

    suspend inline fun <reified T> safeCall(
        endpoint: String,
        crossinline execute: suspend () -> HttpResponse,
    ): Result<T, DataError.Remote> {
        NetworkLogger.d("→ GET $endpoint")
        val response = try {
            execute()
        } catch (e: SocketTimeoutException) {
            NetworkLogger.e("✗ GET $endpoint — socket timeout", e)
            return Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        } catch (e: HttpRequestTimeoutException) {
            NetworkLogger.e("✗ GET $endpoint — request timeout", e)
            return Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        } catch (e: UnresolvedAddressException) {
            NetworkLogger.e("✗ GET $endpoint — no internet / bad host", e)
            return Result.Error(DataError.Remote.NO_INTERNET)
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            NetworkLogger.e("✗ GET $endpoint — ${e::class.simpleName}: ${e.message}", e)
            return Result.Error(DataError.Remote.UNKNOWN)
        }

        return responseToResult(endpoint, response)
    }

    suspend inline fun <reified T> responseToResult(
        endpoint: String,
        response: HttpResponse,
    ): Result<T, DataError.Remote> {
        val url = response.request.url.toString()
        val status = response.status.value
        val rawBody = runCatching { response.bodyAsText() }.getOrElse { "" }
        NetworkLogger.d("← $status $url")

        val failure = failureMapper.map(status, rawBody)

        return when (status) {
            in 200..299 -> {
                if (failure.apiCode != null) {
                    NetworkLogger.e(
                        "✗ GET $endpoint — API fail: ${failure.apiCode} — ${failure.apiMessage}",
                    )
                    return failure.toResultError()
                }
                try {
                    val parsed: T = apiResponseJson.decodeFromString(rawBody)
                    NetworkLogger.d("✓ GET $endpoint — parsed ${T::class.simpleName}")
                    Result.Success(parsed)
                } catch (e: Exception) {
                    NetworkLogger.e(
                        "✗ GET $endpoint — serialization failed: ${e.message}\n" +
                            "Raw body (first 2k): ${rawBody.take(2000)}",
                        e,
                    )
                    Result.Error(DataError.Remote.SERIALIZATION)
                }
            }
            else -> {
                NetworkLogger.e(
                    "✗ GET $endpoint — HTTP $status\nRaw body: ${rawBody.take(2000)}",
                )
                failure.toResultError()
            }
        }
    }

}

fun RemoteFailure.toResultError(): Result.Error<DataError.Remote> =
    Result.Error(error = error, message = apiMessage)

suspend inline fun <reified T> safeCall(
    endpoint: String,
    crossinline execute: suspend () -> HttpResponse,
): Result<T, DataError.Remote> = SafeApiClient().safeCall(endpoint, execute)
