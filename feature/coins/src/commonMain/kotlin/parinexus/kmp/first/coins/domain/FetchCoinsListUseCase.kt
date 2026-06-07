package parinexus.kmp.first.coins.domain

import kotlinx.coroutines.flow.Flow
import parinexus.kmp.first.coins.domain.model.CoinInfoModel
import parinexus.kmp.first.coins.domain.repository.CoinsRepository
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.core.domain.cache.CachedData

class FetchCoinsListUseCase(
    private val repository: CoinsRepository,
) {

    operator fun invoke(forceRefresh: Boolean = false): Flow<Result<CachedData<List<CoinInfoModel>>, DataError.Remote>> =
        repository.observeCoinsList(forceRefresh)
}
