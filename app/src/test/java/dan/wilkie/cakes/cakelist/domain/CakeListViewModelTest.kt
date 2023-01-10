package dan.wilkie.cakes.cakelist.domain

import dan.wilkie.cakes.common.domain.Lce.*
import dan.wilkie.cakes.common.domain.collectInList
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CakeListViewModelTest {
    private val repo = mockk<CakeListRepository>()
    private val viewModel = CakeListViewModel(repo)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `displays cakes when load succeeds`() = runBlocking {
        every { repo.data } returns flowOf(Loading, Content(cakes))

        val displayed = viewModel.data.collectInList()

        assertEquals(
            listOf(Loading, Content(cakes)),
            displayed
        )
    }

    @Test
    fun `displays fresh cakes when refresh succeeds`() = runTest(UnconfinedTestDispatcher()) {
        every { repo.data } returns flowOf(Loading, Content(cakes), Content(freshCakes))
        coEvery { repo.refresh() } just Runs

        val displayed = viewModel.data.collectInList()
        viewModel.refresh()

        assertEquals(listOf(Loading, Content(cakes), Content(freshCakes)), displayed)
    }

    @Test
    fun `displays initial error when initial load fails`() = runBlocking {
        every { repo.data } returns flowOf(Loading, Error(initialError))

        val displayed = viewModel.data.collectInList()

        assertEquals(
            listOf(Loading, Error(initialError)),
            displayed
        )
    }

    @Test
    fun `displays refresh error when refresh fails`() = runTest(UnconfinedTestDispatcher()) {
        every { repo.data } returns flowOf(Loading, Content(cakes))
        coEvery { repo.refresh() } throws refreshError

        val displayed = viewModel.data.collectInList()
        val refreshErrors = viewModel.refreshErrors.collectInList()
        viewModel.refresh()

        assertEquals(listOf(Loading, Content(cakes)), displayed)
        assertEquals(listOf(refreshError), refreshErrors)
    }

    companion object {
        private val cakes = listOf(Cake("title1"), Cake("title2"))
        private val freshCakes = listOf(Cake("title1"), Cake("title2"), Cake("title3"))
        private val initialError = NullPointerException()
        private val refreshError = IllegalArgumentException()
    }
}