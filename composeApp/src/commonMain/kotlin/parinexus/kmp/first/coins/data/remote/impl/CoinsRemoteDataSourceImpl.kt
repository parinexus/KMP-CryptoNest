package parinexus.kmp.first.coins.data.remote.impl

import parinexus.kmp.first.coins.data.remote.dto.CoinDetailsResponseDto
import parinexus.kmp.first.coins.data.remote.dto.CoinPriceHistoryResponseDto
import parinexus.kmp.first.coins.data.remote.dto.CoinsResponseDto
import parinexus.kmp.first.coins.domain.api.CoinsRemoteDataSource
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.core.network.safeCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import parinexus.kmp.first.AppSecrets

class CoinsRemoteDataSourceImpl(
    private val httpClient: HttpClient
) : CoinsRemoteDataSource {

    override suspend fun getListOfCoins(): Result<CoinsResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get("${AppSecrets.baseUrl}/coins")
        }
    }

    override suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get("${AppSecrets.baseUrl}/coin/$coinId/history")
        }
    }

    override suspend fun getCoinById(coinId: String): Result<CoinDetailsResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get("${AppSecrets.baseUrl}/coin/$coinId")
        }
    }
}