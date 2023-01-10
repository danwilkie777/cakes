package dan.wilkie.cakes.common.domain

import dan.wilkie.cakes.common.domain.Lce.Content
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RepositoryTest {
    private val scheduledResults = mutableListOf<Result<String>>()
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

        val emittedItems = repo.data.collectInList()

        assertEquals(listOf(Content(data)), emittedItems)
    }

    @Test
    fun `emits cached data when data is observed`() {
        scheduledResults.add(Result.success(data))

        val emittedItems = repo.data.collectInList()
        val emittedItems2 = repo.data.collectInList()

        assertEquals(listOf(Content(data)), emittedItems)
        assertEquals(listOf(Content(data)), emittedItems2)
        assertEquals(1, requestCount)
    }

    @Test
    fun `emits fresh data data when refresh succeeds`() = runTest {
        scheduledResults.add(Result.success(data))
        scheduledResults.add(Result.success(freshData))

        val emittedItems = repo.data.collectInList()
        repo.refresh()

        assertEquals(listOf(Content(data), Content(freshData)), emittedItems)
    }

    companion object {
        private const val data = "data"
        private const val freshData = "freshData"
    }
}