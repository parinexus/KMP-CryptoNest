package parinexus.kmp.first.coins.domain

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.test.fake.FakeCoinsRemoteDataSource
import parinexus.kmp.first.test.fixture.TestCoins

class FetchCoinsListUseCaseTest {

    private val remote = FakeCoinsRemoteDataSource()
    private val useCase = FetchCoinsListUseCase(remote)

    @Test
    fun execute_returnsMappedCoinsOnSuccess() = runTest {
        val result = useCase.execute()

        assertThat(result is Result.Success).isTrue()
        val coins = (result as Result.Success).data
        assertThat(coins).hasSize(2)
        assertThat(coins.first().coin.id).isEqualTo(TestCoins.BITCOIN_ID)
    }

    @Test
    fun execute_propagatesRemoteError() = runTest {
        remote.setCoinsResult(Result.Error(DataError.Remote.NO_INTERNET))

        val result = useCase.execute()

        assertThat(result).isEqualTo(Result.Error(DataError.Remote.NO_INTERNET))
    }
}
