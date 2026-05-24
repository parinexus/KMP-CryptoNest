package parinexus.kmp.first.coins.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.test.fake.FakeCoinsRemoteDataSource
import parinexus.kmp.first.test.fixture.TestCoins

class FetchCoinDetailsUseCaseTest {

    private val remote = FakeCoinsRemoteDataSource()
    private val useCase = FetchCoinDetailsUseCase(remote)

    @Test
    fun execute_returnsCoinDetails() = runTest {
        val result = useCase.execute(TestCoins.BITCOIN_ID)

        assertThat(result is Result.Success).isTrue()
        val coin = (result as Result.Success).data
        assertThat(coin.coin.name).isEqualTo("Bitcoin")
        assertThat(coin.price).isEqualTo(50_000.0)
    }

    @Test
    fun execute_propagatesRemoteError() = runTest {
        remote.setCoinDetailsResult(Result.Error(DataError.Remote.SERVER))

        val result = useCase.execute(TestCoins.BITCOIN_ID)

        assertThat(result).isEqualTo(Result.Error(DataError.Remote.SERVER))
    }
}
