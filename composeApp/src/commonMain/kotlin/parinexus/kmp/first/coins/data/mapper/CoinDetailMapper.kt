package parinexus.kmp.first.coins.data.mapper

import parinexus.kmp.first.coins.data.remote.dto.CoinDetailDto
import parinexus.kmp.first.coins.domain.model.CoinDetailModel
import parinexus.kmp.first.coins.domain.model.CoinInfoModel
import parinexus.kmp.first.coins.domain.model.CoinNoticeModel
import parinexus.kmp.first.core.domain.coin.Coin
import parinexus.kmp.first.core.util.HtmlNoticeParser

fun CoinDetailDto.toCoinDetailModel(): CoinDetailModel = CoinDetailModel(
    coin = Coin(
        id = uuid,
        name = name,
        symbol = symbol,
        iconUrl = iconUrl,
    ),
    description = description.orEmpty(),
    price = price.toDoubleOrNull() ?: 0.0,
    changePercent = change.toDoubleOrNull() ?: 0.0,
    rank = rank,
    marketCap = marketCap?.toDoubleOrNull(),
    volume24h = volume24h?.toDoubleOrNull(),
    sparkline = sparkline.orEmpty().mapNotNull { it.toDoubleOrNull() },
    circulatingSupply = supply?.circulating?.toDoubleOrNull(),
    maxSupply = supply?.max?.toDoubleOrNull(),
    allTimeHighPrice = allTimeHigh?.price?.toDoubleOrNull(),
    numberOfMarkets = numberOfMarkets,
    numberOfExchanges = numberOfExchanges,
    tags = tags.orEmpty(),
    notices = notices.orEmpty().mapNotNull { notice ->
        val segments = HtmlNoticeParser.parse(notice.value.orEmpty())
        if (segments.isEmpty()) return@mapNotNull null
        CoinNoticeModel(
            type = notice.type.orEmpty(),
            segments = segments,
        )
    },
    websiteUrl = websiteUrl?.takeIf { it.isNotBlank() },
)

fun CoinDetailModel.toCoinInfoModel(): CoinInfoModel = CoinInfoModel(
    coin = coin,
    price = price,
    changePercent = changePercent,
)
