package dan.wilkie.cakes.common.domain

import dan.wilkie.cakes.common.domain.Lce.Content
import dan.wilkie.cakes.common.domain.Lce.Loading
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RepositoryTest {
    private val scheduledResults = mutableListOf<Result<String>>()
    private val emittedItems = mutableListOf<Lce<String>>()
    private val emittedItems2 = mutableListOf<Lce<String>>()
    private var requestCount = 0

    private val repo = Repository(
        request = {
            requestCount++
            scheduledResults.removeFirst().getOrThrow()
        }
    )

    @Test
    fun `emits data when request succeeds`() {
        scheduledResults.add(Result.success(data))
        scheduledResults.add(Result.success(data))

        repo.data.collectIn(emittedItems)

        assertEquals(listOf(Content(data)), emittedItems)
    }

    @Test
    fun `emits cached data when data is observed`() {
        scheduledResults.add(Result.success(data))

        repo.data.collectIn(emittedItems)
        repo.data.collectIn(emittedItems2)

        assertEquals(listOf(Content(data)), emittedItems)
        assertEquals(listOf(Content(data)), emittedItems2)
        assertEquals(1, requestCount)
    }

    @Test
    fun `emits fresh data data when refresh succeeds`() = runTest {
        scheduledResults.add(Result.success(data))
        scheduledResults.add(Result.success(freshData))

        repo.data.collectIn(emittedItems)
        repo.refresh()

        assertEquals(listOf(Content(data), Content(freshData)), emittedItems)
    }

    private fun Flow<Lce<String>>.collectIn(destination: MutableList<Lce<String>>) =
        onEach { destination.add(it) }
            .launchIn(CoroutineScope(UnconfinedTestDispatcher()))

    companion object {
        private const val data = "data"
        private const val freshData = "freshData"
    }
}