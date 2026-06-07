package parinexus.kmp.first.test.fixture

import parinexus.kmp.first.coins.data.remote.dto.CoinAllTimeHighDto
import parinexus.kmp.first.coins.data.remote.dto.CoinDetailDto
import parinexus.kmp.first.coins.data.remote.dto.CoinDetailsResponseDto
import parinexus.kmp.first.coins.data.remote.dto.CoinItemDto
import parinexus.kmp.first.coins.data.remote.dto.CoinSupplyDto
import parinexus.kmp.first.coins.data.remote.dto.CoinPriceDto
import parinexus.kmp.first.coins.data.remote.dto.CoinPriceHistoryDto
import parinexus.kmp.first.coins.data.remote.dto.CoinPriceHistoryResponseDto
import parinexus.kmp.first.coins.data.remote.dto.CoinResponseDto
import parinexus.kmp.first.coins.data.remote.dto.CoinsListDto
import parinexus.kmp.first.coins.data.remote.dto.CoinsResponseDto
import parinexus.kmp.first.core.domain.coin.Coin

object TestCoins {
    const val BITCOIN_ID = "bitcoin-id"
    const val ETHEREUM_ID = "ethereum-id"

    val bitcoin = Coin(
        id = BITCOIN_ID,
        name = "Bitcoin",
        symbol = "BTC",
        iconUrl = "https://example.com/btc.png",
    )

    val ethereum = Coin(
        id = ETHEREUM_ID,
        name = "Ethereum",
        symbol = "ETH",
        iconUrl = "https://example.com/eth.png",
    )

    val bitcoinDto = CoinItemDto(
        uuid = BITCOIN_ID,
        symbol = "BTC",
        name = "Bitcoin",
        iconUrl = bitcoin.iconUrl,
        price = "50000",
        rank = 1,
        change = "2.5",
    )

    val ethereumDto = CoinItemDto(
        uuid = ETHEREUM_ID,
        symbol = "ETH",
        name = "Ethereum",
        iconUrl = ethereum.iconUrl,
        price = "3000",
        rank = 2,
        change = "-1.2",
    )

    val coinsResponse = CoinsResponseDto(
        data = CoinsListDto(
            coins = listOf(bitcoinDto, ethereumDto),
        ),
    )

    val bitcoinDetailDto = CoinDetailDto(
        uuid = BITCOIN_ID,
        symbol = "BTC",
        name = "Bitcoin",
        description = "Bitcoin is the first decentralized digital currency.",
        iconUrl = bitcoin.iconUrl,
        price = "50000",
        rank = 1,
        change = "2.5",
        marketCap = "1000000000000",
        volume24h = "50000000000",
        sparkline = listOf("48000", "49000", "50000"),
        supply = CoinSupplyDto(
            circulating = "19000000",
            max = "21000000",
        ),
        allTimeHigh = CoinAllTimeHighDto(price = "69000", timestamp = 1_609_459_200L),
        numberOfMarkets = 9_800,
        numberOfExchanges = 190,
        tags = listOf("layer-1"),
    )

    val bitcoinDetailsResponse = CoinDetailsResponseDto(
        data = CoinResponseDto(coin = bitcoinDetailDto),
    )

    val priceHistoryResponse = CoinPriceHistoryResponseDto(
        data = CoinPriceHistoryDto(
            history = listOf(
                CoinPriceDto(price = "48000", timestamp = 1L),
                CoinPriceDto(price = "49000", timestamp = 2L),
                CoinPriceDto(price = "50000", timestamp = 3L),
            ),
        ),
    )
}
