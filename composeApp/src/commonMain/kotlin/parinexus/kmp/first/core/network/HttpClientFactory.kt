package parinexus.kmp.first.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import parinexus.kmp.first.AppSecrets

object HttpClientFactory {
    fun create(engine: HttpClientEngine): HttpClient {
        val apiKeyConfigured = AppSecrets.apiKey.isNotBlank()
        NetworkLogger.d(
            "HttpClient config: baseUrl=${AppSecrets.baseUrl}, " +
                "apiKey=${NetworkLogger.maskSecret(AppSecrets.apiKey)}, " +
                "apiKeyConfigured=$apiKeyConfigured",
        )
        if (!apiKeyConfigured) {
            NetworkLogger.e("API_KEY is empty — Coinranking requests will fail")
        }

        return HttpClient(engine) {
            install(HttpRequestRetry) {
                maxRetries = 2
                retryOnException(maxRetries = 2, retryOnTimeout = true)
                exponentialDelay()
            }
            install(HttpTimeout) {
                connectTimeoutMillis = 30_000L
                socketTimeoutMillis = 60_000L
                requestTimeoutMillis = 60_000L
            }
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    },
                )
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        NetworkLogger.d(message)
                    }
                }
                level = LogLevel.ALL
                sanitizeHeader { header -> header.equals("x-access-token", ignoreCase = true) }
            }
            defaultRequest {
                url {
                    takeFrom(AppSecrets.baseUrl)
                }
                headers { append("x-access-token", AppSecrets.apiKey) }
                contentType(ContentType.Application.Json)
            }
        }
    }
}
