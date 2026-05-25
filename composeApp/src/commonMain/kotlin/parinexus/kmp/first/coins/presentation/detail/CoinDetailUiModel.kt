package parinexus.kmp.first.coins.presentation.detail

data class CoinDetailUiModel(
    val coinId: String,
    val name: String,
    val symbol: String,
    val iconUrl: String,
    val rankLabel: String,
    val formattedPrice: String,
    val formattedChange: String,
    val isPositiveChange: Boolean,
    val marketCapLabel: String,
    val volume24hLabel: String,
    val marketsLabel: String,
    val exchangesLabel: String,
    val supplyLabel: String,
    val allTimeHighLabel: String,
    val description: String,
    val tags: List<String>,
    val notices: List<CoinNoticeUiModel>,
    val websiteUrl: String?,
)
