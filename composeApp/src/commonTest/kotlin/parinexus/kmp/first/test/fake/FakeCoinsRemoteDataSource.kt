package parinexus.kmp.first.test.fake

import parinexus.kmp.first.coins.data.remote.dto.CoinDetailsResponseDto
import parinexus.kmp.first.coins.data.remote.dto.CoinPriceHistoryResponseDto
import parinexus.kmp.first.coins.data.remote.dto.CoinsResponseDto
import parinexus.kmp.first.coins.domain.api.CoinsRemoteDataSource
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.test.fixture.TestCoins

class FakeCoinsRemoteDataSource(
    private var coinsResult: Result<CoinsResponseDto, DataError.Remote> =
        Result.Success(TestCoins.coinsResponse),
    private var coinDetailsResult: Result<CoinDetailsResponseDto, DataError.Remote> =
        Result.Success(TestCoins.bitcoinDetailsResponse),
    private var priceHistoryResult: Result<CoinPriceHistoryResponseDto, DataError.Remote> =
        Result.Success(TestCoins.priceHistoryResponse),
) : CoinsRemoteDataSource {

    var priceHistoryRequests: MutableList<String> = mutableListOf()
        private set

    fun setCoinsResult(result: Result<CoinsResponseDto, DataError.Remote>) {
        coinsResult = result
    }

    fun setCoinDetailsResult(result: Result<CoinDetailsResponseDto, DataError.Remote>) {
        coinDetailsResult = result
    }

    fun setPriceHistoryResult(result: Result<CoinPriceHistoryResponseDto, DataError.Remote>) {
        priceHistoryResult = result
    }

    override suspend fun getListOfCoins(): Result<CoinsResponseDto, DataError.Remote> = coinsResult

    override suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponseDto, DataError.Remote> {
        priceHistoryRequests.add(coinId)
        return priceHistoryResult
    }

    override suspend fun getCoinById(coinId: String): Result<CoinDetailsResponseDto, DataError.Remote> =
        coinDetailsResult
}
