package parinexus.kmp.first.core.domain.cache

data class CachedData<T>(
    val value: T,
    val freshness: DataFreshness,
    val cachedAtEpochMs: Long?,
)
