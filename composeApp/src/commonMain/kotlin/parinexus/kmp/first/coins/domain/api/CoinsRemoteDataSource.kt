package parinexus.kmp.first.coins.domain.api

import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.coins.data.remote.dto.CoinDetailsResponseDto
import parinexus.kmp.first.coins.data.remote.dto.CoinPriceHistoryResponseDto
import parinexus.kmp.first.coins.data.remote.dto.CoinsResponseDto
import parinexus.kmp.first.core.domain.DataError

interface CoinsRemoteDataSource {

    suspend fun getListOfCoins(): Result<CoinsResponseDto, DataError.Remote>

    suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponseDto, DataError.Remote>

    suspend fun getCoinById(coinId: String): Result<CoinDetailsResponseDto, DataError.Remote>
}