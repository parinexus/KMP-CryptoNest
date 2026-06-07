package parinexus.kmp.first.test.rule

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
open class MainCoroutineRule {
    private val testDispatcher = UnconfinedTestDispatcher()

    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    fun tearDown() {
        Dispatchers.resetMain()
    }
}
