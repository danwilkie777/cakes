package dan.wilkie.cakes.cakelist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dan.wilkie.cakes.cakelist.domain.Cake
import dan.wilkie.cakes.cakelist.domain.CakeListRepository
import dan.wilkie.cakes.cakelist.ui.CakeListViewState.Content
import dan.wilkie.cakes.cakelist.ui.CakeListViewState.Error
import dan.wilkie.cakes.cakelist.ui.CakeListViewState.Loading
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CakeListViewModel(private val repo: CakeListRepository) : ViewModel() {
    private val initialLoadState = MutableStateFlow<CakeListViewState>(Loading)
    private val isRefreshing = MutableStateFlow(false)
    private val displayRefreshError = MutableStateFlow(false)

    private val content: Flow<CakeListViewState> =
        combine(repo.data, isRefreshing, displayRefreshError) { cakes, refreshing, refreshError ->
                Content(cakes, refreshing, refreshError)
            }

    val viewState: Flow<CakeListViewState> = merge(
        initialLoadState,
        content
    )

    init {
        initialLoad()
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing.emit(true)
            try {
                repo.refresh()
            } catch (throwable: Throwable) {
                displayRefreshError.emit(true)
            }
            isRefreshing.emit(false)
        }
    }

    fun initialLoad() {
        viewModelScope.launch {
            initialLoadState.emit(Loading)
            try {
                repo.initialLoad()
            } catch (throwable: Throwable) {
                initialLoadState.emit(Error)
            }
        }
    }

    fun refreshErrorShown() {
        viewModelScope.launch {
            displayRefreshError.emit(false)
        }
    }
}

sealed interface CakeListViewState {
    data object Loading : CakeListViewState
    data class Content(
        val cakes: List<Cake>,
        val refreshing: Boolean = false,
        val displayRefreshError: Boolean = false
    ) : CakeListViewState
    data object Error : CakeListViewState
}