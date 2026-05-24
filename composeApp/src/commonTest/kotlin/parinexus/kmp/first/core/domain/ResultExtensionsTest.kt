package parinexus.kmp.first.core.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import kotlin.test.Test

class ResultExtensionsTest {

    @Test
    fun map_transformsSuccessValue() {
        val result: Result<Int, DataError.Remote> = Result.Success(2)

        val mapped = result.map { it * 3 }

        assertThat(mapped is Result.Success).isTrue()
        assertThat((mapped as Result.Success).data).isEqualTo(6)
    }

    @Test
    fun map_preservesError() {
        val result: Result<Int, DataError.Remote> = Result.Error(DataError.Remote.NO_INTERNET)

        val mapped = result.map { it * 3 }

        assertThat(mapped).isEqualTo(Result.Error(DataError.Remote.NO_INTERNET))
    }

    @Test
    fun onSuccess_runsActionOnlyForSuccess() {
        var actionValue: Int? = null
        val result: Result<Int, DataError.Remote> = Result.Success(42)

        result.onSuccess { actionValue = it }

        assertThat(actionValue).isEqualTo(42)
    }

    @Test
    fun onError_runsActionOnlyForError() {
        var captured: DataError.Remote? = null
        val result: Result<Int, DataError.Remote> = Result.Error(DataError.Remote.SERVER)

        result.onError { captured = it }

        assertThat(captured).isEqualTo(DataError.Remote.SERVER)
    }

    @Test
    fun asEmptyDataResult_mapsSuccessToUnit() {
        val result: Result<String, DataError.Local> = Result.Success("ok")

        val empty = result.asEmptyDataResult()

        assertThat(empty).isEqualTo(Result.Success(Unit))
    }
}
