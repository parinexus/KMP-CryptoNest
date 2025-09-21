package parinexus.kmp.first.coins.domain.model

import parinexus.kmp.first.core.domain.coin.Coin

data class CoinInfoModel(
    val coin: Coin,
    val price: Double,
    val changePercent: Double,
)