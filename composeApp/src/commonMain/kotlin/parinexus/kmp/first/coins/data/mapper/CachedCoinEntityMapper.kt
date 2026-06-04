package parinexus.kmp.first.coins.data.mapper

import parinexus.kmp.first.coins.data.local.CachedCoinEntity
import parinexus.kmp.first.coins.domain.model.CoinInfoModel
import parinexus.kmp.first.core.domain.coin.Coin

fun CoinInfoModel.toCachedCoinEntity(listOrder: Int): CachedCoinEntity = CachedCoinEntity(
    id = coin.id,
    name = coin.name,
    symbol = coin.symbol,
    iconUrl = coin.iconUrl,
    price = price,
    changePercent = changePercent,
    rank = 0,
    listOrder = listOrder,
)

fun CachedCoinEntity.toCoinInfoModel(): CoinInfoModel = CoinInfoModel(
    coin = Coin(
        id = id,
        name = name,
        symbol = symbol,
        iconUrl = iconUrl,
    ),
    price = price,
    changePercent = changePercent,
)
