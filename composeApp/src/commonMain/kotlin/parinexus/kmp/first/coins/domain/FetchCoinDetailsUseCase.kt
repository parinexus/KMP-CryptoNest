package parinexus.kmp.first.coins.domain

import parinexus.kmp.first.coins.data.mapper.toCoinDetailModel
import parinexus.kmp.first.coins.domain.api.CoinsRemoteDataSource
import parinexus.kmp.first.coins.domain.model.CoinDetailModel
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.core.domain.map

class FetchCoinDetailsUseCase(
    private val client: CoinsRemoteDataSource,
) {

    suspend fun execute(coinId: String): Result<CoinDetailModel, DataError.Remote> {
        return client.getCoinById(coinId).map { dto ->
            dto.data.coin.toCoinDetailModel()
        }
    }
}