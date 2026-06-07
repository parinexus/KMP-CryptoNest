package parinexus.kmp.first.core.domain.cache

/**
 * Describes how current UI data relates to the network and local cache.
 */
enum class DataFreshness {
    /** Loaded from network and written to cache. */
    Fresh,

    /** Shown from cache while a refresh is in flight, or cache within TTL but not just fetched. */
    Cached,

    /** Network failed; only local cache is available. */
    Offline,

    /** Cache exists but is older than [MarketCachePolicy] TTL. */
    Stale,
}
