package parinexus.kmp.first.trade.presentation.mapper

import parinexus.kmp.first.core.domain.coin.Coin
import parinexus.kmp.first.trade.presentation.common.UiTradeCoinItem

fun UiTradeCoinItem.toCoin() = Coin(
    id = id,
    name = name,
    symbol = symbol,
    iconUrl = iconUrl,
)