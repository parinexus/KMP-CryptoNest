package parinexus.kmp.first.coins.domain

import parinexus.kmp.first.coins.domain.repository.CoinsRepository
import parinexus.kmp.first.coins.domain.model.PriceModel
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.core.domain.cache.CachedData

class FetchCoinPriceHistoryUseCase(
    private val repository: CoinsRepository,
) {

    suspend fun execute(
        coinId: String,
        forceRefresh: Boolean = false,
    ): Result<CachedData<List<PriceModel>>, DataError.Remote> =
        repository.getPriceHistory(coinId, forceRefresh)
}
