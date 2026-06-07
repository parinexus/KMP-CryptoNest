package parinexus.kmp.first.coins.domain

import kotlinx.coroutines.flow.Flow
import parinexus.kmp.first.coins.domain.model.CoinDetailModel
import parinexus.kmp.first.coins.domain.repository.CoinsRepository
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.core.domain.cache.CachedData

class FetchCoinDetailsUseCase(
    private val repository: CoinsRepository,
) {

    operator fun invoke(
        coinId: String,
        forceRefresh: Boolean = false,
    ): Flow<Result<CachedData<CoinDetailModel>, DataError.Remote>> =
        repository.observeCoinDetail(coinId, forceRefresh)
}
