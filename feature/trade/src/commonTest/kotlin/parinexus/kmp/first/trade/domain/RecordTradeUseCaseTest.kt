package parinexus.kmp.first.trade.domain

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import parinexus.kmp.first.core.domain.DataError
import parinexus.kmp.first.core.domain.Result
import parinexus.kmp.first.test.fake.FakeTradeHistoryRepository
import parinexus.kmp.first.test.fixture.TestTrades

class RecordTradeUseCaseTest {

    @Test
    fun execute_persistsTradeDraft() = runTest {
        val repository = FakeTradeHistoryRepository()
        val useCase = RecordTradeUseCase(repository)

        val result = useCase.execute(TestTrades.bitcoinBuyDraft)

        assertThat(result is Result.Success).isTrue()
        assertThat(repository.recordedDrafts).hasSize(1)
        assertThat(repository.recordedDrafts.first()).isEqualTo(TestTrades.bitcoinBuyDraft)
    }

    @Test
    fun execute_propagatesRepositoryError() = runTest {
        val repository = FakeTradeHistoryRepository(
            recordResult = Result.Error(DataError.Local.DISK_FULL),
        )
        val useCase = RecordTradeUseCase(repository)

        val result = useCase.execute(TestTrades.bitcoinBuyDraft)

        assertThat(result).isEqualTo(Result.Error(DataError.Local.DISK_FULL))
    }
}
