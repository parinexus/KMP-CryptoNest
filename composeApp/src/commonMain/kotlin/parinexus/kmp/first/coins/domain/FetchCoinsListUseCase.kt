package parinexus.kmp.first.coins.domain

import parinexus.kmp.first.coins.data.mapper.toCoinInfoModel
import parinexus.kmp.first.coins.domain.api.CoinsRemoteDataSource
import parinexus.kmp.first.coins.domain.model.CoinInfoModel
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.core.domain.map

class FetchCoinsListUseCase(
    private val client: CoinsRemoteDataSource,
) {

    suspend fun execute(): Result<List<CoinInfoModel>, DataError.Remote> {
        return client.getListOfCoins().map { dto ->
            dto.data.coins.map { it.toCoinInfoModel() }
        }
    }
}