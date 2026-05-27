package parinexus.kmp.first.core.presentation.cache

import kmp_cryptonest.composeapp.generated.resources.Res
import kmp_cryptonest.composeapp.generated.resources.market_cache_cached
import kmp_cryptonest.composeapp.generated.resources.market_cache_offline
import kmp_cryptonest.composeapp.generated.resources.market_cache_stale
import org.jetbrains.compose.resources.StringResource
import parinexus.kmp.first.core.domain.cache.DataFreshness

object DataFreshnessUiMapper {

    fun bannerMessage(freshness: DataFreshness): StringResource? = when (freshness) {
        DataFreshness.Fresh -> null
        DataFreshness.Cached -> Res.string.market_cache_cached
        DataFreshness.Stale -> Res.string.market_cache_stale
        DataFreshness.Offline -> Res.string.market_cache_offline
    }
}
