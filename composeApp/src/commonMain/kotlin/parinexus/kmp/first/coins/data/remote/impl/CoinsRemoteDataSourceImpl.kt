package parinexus.kmp.first.coins.data.remote.impl

import parinexus.kmp.first.coins.data.remote.dto.CoinDetailsResponseDto
import parinexus.kmp.first.coins.data.remote.dto.CoinPriceHistoryResponseDto
import parinexus.kmp.first.coins.data.remote.dto.CoinsResponseDto
import parinexus.kmp.first.coins.domain.api.CoinsRemoteDataSource
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.core.domain.onError
import parinexus.kmp.first.core.domain.onSuccess
import parinexus.kmp.first.core.network.NetworkLogger
import parinexus.kmp.first.core.api.data.client.safeCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class CoinsRemoteDataSourceImpl(
    private val httpClient: HttpClient,
) : CoinsRemoteDataSource {

    override suspend fun getListOfCoins(): Result<CoinsResponseDto, DataError.Remote> {
        return safeCall<CoinsResponseDto>("coins") {
            httpClient.get("coins") {
                parameter("limit", "50")
            }
        }.also { result ->
            result.onSuccess { dto ->
                NetworkLogger.d(
                    "coins list: count=${dto.data.coins.size}, " +
                        "first=${dto.data.coins.firstOrNull()?.name}",
                )
            }.onError { error ->
                NetworkLogger.e("coins list failed: $error")
            }
        }
    }

    override suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponseDto, DataError.Remote> {
        return safeCall<CoinPriceHistoryResponseDto>("coin/$coinId/price-history") {
            httpClient.get("coin/$coinId/price-history") {
                parameter("timePeriod", "24h")
            }
        }.also { result ->
            result.onSuccess { dto ->
                NetworkLogger.d(
                    "price history: coinId=$coinId, points=${dto.data.history.size}",
                )
            }.onError { error ->
                NetworkLogger.e("price history failed: coinId=$coinId, error=$error")
            }
        }
    }

    override suspend fun getCoinById(coinId: String): Result<CoinDetailsResponseDto, DataError.Remote> {
        return safeCall("coin/$coinId") {
            httpClient.get("coin/$coinId")
        }
    }
}
