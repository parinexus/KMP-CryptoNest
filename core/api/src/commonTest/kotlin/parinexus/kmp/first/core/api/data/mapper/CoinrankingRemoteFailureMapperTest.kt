package parinexus.kmp.first.core.api.data.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test
import parinexus.kmp.first.core.domain.DataError

class CoinrankingRemoteFailureMapperTest {

    private val mapper = CoinrankingRemoteFailureMapper()

    @Test
    fun map_rateLimit_withCodeField_returnsTooManyRequests() {
        val body = """
            {"status":"fail","code":"RATE_LIMIT_EXCEEDED","message":"You've reached the API request limit."}
        """.trimIndent()

        val failure = mapper.map(httpStatus = 429, body = body)

        assertThat(failure.error).isEqualTo(DataError.Remote.TOO_MANY_REQUESTS)
        assertThat(failure.apiMessage).isEqualTo("You've reached the API request limit.")
        assertThat(failure.apiCode).isEqualTo("RATE_LIMIT_EXCEEDED")
    }

    @Test
    fun map_coinNotFound_withTypeField_returnsCoinNotFound() {
        val body = """
            {"status":"fail","type":"COIN_NOT_FOUND","message":"Coin not found"}
        """.trimIndent()

        val failure = mapper.map(httpStatus = 404, body = body)

        assertThat(failure.error).isEqualTo(DataError.Remote.COIN_NOT_FOUND)
        assertThat(failure.apiMessage).isEqualTo("Coin not found")
        assertThat(failure.apiCode).isEqualTo("COIN_NOT_FOUND")
    }

    @Test
    fun map_validationError_422_returnsValidationError() {
        val body = """
            {"status":"fail","type":"VALIDATION_ERROR","message":"timePeriod is not valid"}
        """.trimIndent()

        val failure = mapper.map(httpStatus = 422, body = body)

        assertThat(failure.error).isEqualTo(DataError.Remote.VALIDATION_ERROR)
        assertThat(failure.apiMessage).isEqualTo("timePeriod is not valid")
        assertThat(failure.apiCode).isEqualTo("VALIDATION_ERROR")
    }

    @Test
    fun map_referenceUnavailable_422_returnsReferenceUnavailable() {
        val body = """
            {"status":"fail","type":"REFERENCE_UNAVAILABLE","message":"Reference currency not available"}
        """.trimIndent()

        val failure = mapper.map(httpStatus = 422, body = body)

        assertThat(failure.error).isEqualTo(DataError.Remote.REFERENCE_UNAVAILABLE)
        assertThat(failure.apiMessage).isEqualTo("Reference currency not available")
        assertThat(failure.apiCode).isEqualTo("REFERENCE_UNAVAILABLE")
    }
}
