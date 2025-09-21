package parinexus.kmp.first.coins.data.mapper

import parinexus.kmp.first.coins.data.remote.dto.CoinItemDto
import parinexus.kmp.first.coins.data.remote.dto.CoinPriceDto
import parinexus.kmp.first.coins.domain.model.CoinInfoModel
import parinexus.kmp.first.coins.domain.model.PriceModel
import parinexus.kmp.first.core.domain.coin.Coin

fun CoinItemDto.toCoinInfoModel() = CoinInfoModel(
    coin = Coin(
        id = uuid,
        name = name,
        symbol = symbol,
        iconUrl = iconUrl,
    ),
    price = price,
    changePercent = change,
)

fun CoinPriceDto.toPriceModel() = PriceModel(
    price = price ?: 0.0,
    timestamp = timestamp,
)