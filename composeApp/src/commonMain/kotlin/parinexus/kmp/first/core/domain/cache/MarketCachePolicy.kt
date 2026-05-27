package parinexus.kmp.first.core.domain.cache

import kotlinx.datetime.Clock

object MarketCachePolicy {
    const val COINS_LIST_KEY = "coins_list"
    const val COIN_DETAIL_KEY_PREFIX = "coin_detail_"
    const val PRICE_HISTORY_KEY_PREFIX = "price_history_"

    /** Coins list and detail prices — 5 minutes. */
    const val MARKET_DATA_TTL_MS = 5 * 60 * 1000L

    /** Intraday chart — 15 minutes. */
    const val CHART_DATA_TTL_MS = 15 * 60 * 1000L

    fun coinDetailKey(coinId: String): String = "$COIN_DETAIL_KEY_PREFIX$coinId"

    fun priceHistoryKey(coinId: String): String = "$PRICE_HISTORY_KEY_PREFIX$coinId"

    fun evaluateFreshness(cachedAtEpochMs: Long?, ttlMs: Long): DataFreshness {
        if (cachedAtEpochMs == null) return DataFreshness.Fresh
        val age = Clock.System.now().toEpochMilliseconds() - cachedAtEpochMs
        return if (age <= ttlMs) DataFreshness.Cached else DataFreshness.Stale
    }
}
