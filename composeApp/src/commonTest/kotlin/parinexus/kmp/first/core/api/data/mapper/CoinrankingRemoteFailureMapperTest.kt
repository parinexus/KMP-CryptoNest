package parinexus.kmp.first.core.api.data.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test
import parinexus.kmp.first.core.domain.DataError

class CoinrankingRemoteFailureMapperTest {

    private val mapper = CoinrankingRemoteFailureMapper()

    @Test
    fun map_rateLimit_returnsTooManyRequestsWithApiMessage() {
        val body = """
            {"status":"fail","code":"RATE_LIMIT_EXCEEDED","message":"You've reached the API request limit."}
        """.trimIndent()

        val failure = mapper.map(httpStatus = 429, body = body)

        assertThat(failure.error).isEqualTo(DataError.Remote.TOO_MANY_REQUESTS)
        assertThat(failure.apiMessage).isEqualTo("You've reached the API request limit.")
        assertThat(failure.apiCode).isEqualTo("RATE_LIMIT_EXCEEDED")
    }
}
