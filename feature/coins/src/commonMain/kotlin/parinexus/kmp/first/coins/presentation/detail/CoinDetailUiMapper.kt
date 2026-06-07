package parinexus.kmp.first.coins.presentation.detail

import parinexus.kmp.first.coins.domain.model.CoinDetailModel
import parinexus.kmp.first.coins.domain.model.CoinNoticeModel
import parinexus.kmp.first.core.util.formatCompactFiat
import parinexus.kmp.first.core.util.formatFiat
import parinexus.kmp.first.core.util.formatPercentage

object CoinDetailUiMapper {

    fun toUiModel(model: CoinDetailModel): CoinDetailUiModel = CoinDetailUiModel(
        coinId = model.coin.id,
        name = model.coin.name,
        symbol = model.coin.symbol,
        iconUrl = model.coin.iconUrl,
        rankLabel = "#${model.rank}",
        formattedPrice = formatFiat(model.price),
        formattedChange = formatPercentage(model.changePercent),
        isPositiveChange = model.changePercent >= 0,
        marketCapLabel = model.marketCap?.let(::formatCompactFiat) ?: "—",
        volume24hLabel = model.volume24h?.let(::formatCompactFiat) ?: "—",
        marketsLabel = model.numberOfMarkets?.toString() ?: "—",
        exchangesLabel = model.numberOfExchanges?.toString() ?: "—",
        supplyLabel = formatSupply(model.circulatingSupply, model.maxSupply),
        allTimeHighLabel = model.allTimeHighPrice?.let(::formatFiat) ?: "—",
        description = model.description.ifBlank { "No description available for this coin." },
        tags = model.tags,
        notices = model.notices.map(::toNoticeUiModel),
        websiteUrl = model.websiteUrl,
    )

    private fun toNoticeUiModel(notice: CoinNoticeModel): CoinNoticeUiModel = CoinNoticeUiModel(
        segments = notice.segments,
        style = when (notice.type.uppercase()) {
            "WARNING" -> CoinNoticeStyle.Warning
            "ALERT", "ERROR" -> CoinNoticeStyle.Alert
            else -> CoinNoticeStyle.Info
        },
    )

    private fun formatSupply(circulating: Double?, max: Double?): String {
        if (circulating == null && max == null) return "—"
        val circulatingText = circulating?.let(::formatCompactFiat) ?: "—"
        val maxText = max?.let(::formatCompactFiat) ?: "—"
        return "$circulatingText / $maxText max"
    }
}
