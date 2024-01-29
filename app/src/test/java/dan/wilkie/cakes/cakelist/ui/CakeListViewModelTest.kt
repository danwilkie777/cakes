package dan.wilkie.cakes.cakelist.ui

import dan.wilkie.cakes.cakelist.domain.Cake
import dan.wilkie.cakes.cakelist.domain.CakeListRepository
import dan.wilkie.cakes.common.domain.Lce
import dan.wilkie.cakes.common.domain.collectInList
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
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
        every { repo.data } returns flowOf(Lce.Loading, Lce.Content(cakes))

        val displayed = viewModel.screenState.collectInList()

        Assert.assertEquals(
            listOf(Lce.Loading, Lce.Content(cakes)),
            displayed
        )
    }

    @Test
    fun `displays fresh cakes when refresh succeeds`() = runTest(UnconfinedTestDispatcher()) {
        every { repo.data } returns flowOf(Lce.Loading, Lce.Content(cakes), Lce.Content(freshCakes))
        coEvery { repo.refresh() } just Runs

        val displayed = viewModel.screenState.collectInList()
        viewModel.refresh()

        Assert.assertEquals(
            listOf(Lce.Loading, Lce.Content(cakes), Lce.Content(freshCakes)),
            displayed
        )
    }

    @Test
    fun `displays initial error when initial load fails`() = runBlocking {
        every { repo.data } returns flowOf(Lce.Loading, Lce.Error)

        val displayed = viewModel.screenState.collectInList()

        Assert.assertEquals(
            listOf(Lce.Loading, Lce.Error),
            displayed
        )
    }

    @Test
    fun `displays refresh error when refresh fails`() = runTest(UnconfinedTestDispatcher()) {
        every { repo.data } returns flowOf(Lce.Loading, Lce.Content(cakes))
        coEvery { repo.refresh() } throws refreshError

        val displayed = viewModel.screenState.collectInList()
        val refreshStates = viewModel.refreshState.collectInList()
        viewModel.refresh()

        Assert.assertEquals(listOf(Lce.Loading, Lce.Content(cakes)), displayed)
        Assert.assertEquals(listOf(RefreshState.IDLE, RefreshState.FAILED), refreshStates)
    }

    companion object {
        private val cakes = listOf(Cake("title1"), Cake("title2"))
        private val freshCakes = listOf(Cake("title1"), Cake("title2"), Cake("title3"))
        private val refreshError = IllegalArgumentException()
    }
}