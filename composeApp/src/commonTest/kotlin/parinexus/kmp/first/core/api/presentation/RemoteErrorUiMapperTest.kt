package parinexus.kmp.first.core.api.presentation

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import kotlin.test.Test
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result

class RemoteErrorUiMapperTest {

    @Test
    fun rateLimit_usesDistinctMessagePerContext() {
        val error = Result.Error(
            error = DataError.Remote.TOO_MANY_REQUESTS,
            message = "You've reached the API request limit.",
        )

        val listMessage = RemoteErrorUiMapper.toDisplayMessage(error, RemoteErrorContext.CoinsList)
        val detailMessage = RemoteErrorUiMapper.toDisplayMessage(error, RemoteErrorContext.CoinDetail)
        val chartMessage = RemoteErrorUiMapper.toDisplayMessage(error, RemoteErrorContext.CoinDetailChart)

        assertThat(listMessage).contains("Coin list:")
        assertThat(detailMessage).contains("Coin details:")
        assertThat(chartMessage).contains("24h chart (detail):")
        assertThat(listMessage).isNotEqualTo(detailMessage)
    }

    @Test
    fun validationError_usesContextualFallbackWhenApiMessageMissing() {
        val error = Result.Error(error = DataError.Remote.VALIDATION_ERROR, message = null)

        val message = RemoteErrorUiMapper.toDisplayMessage(error, RemoteErrorContext.CoinDetail)

        assertThat(message).contains("Coin details:")
        assertThat(message).contains("invalid")
    }

    @Test
    fun validationError_prefersApiMessageWhenPresent() {
        val error = Result.Error(
            error = DataError.Remote.VALIDATION_ERROR,
            message = "timePeriod is not valid",
        )

        val message = RemoteErrorUiMapper.toDisplayMessage(error, RemoteErrorContext.CoinDetail)

        assertThat(message).isEqualTo("timePeriod is not valid")
    }

    @Test
    fun nonRateLimit_prefersApiMessage() {
        val error = Result.Error(
            error = DataError.Remote.UNKNOWN,
            message = "Coin not found",
        )

        val message = RemoteErrorUiMapper.toDisplayMessage(error, RemoteErrorContext.CoinDetail)

        assertThat(message).isEqualTo("Coin not found")
    }
}
