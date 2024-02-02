package dan.wilkie.cakes.cakelist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dan.wilkie.cakes.cakelist.domain.Cake
import dan.wilkie.cakes.cakelist.domain.CakeListRepository
import dan.wilkie.cakes.cakelist.ui.CakeListUiState.Content
import dan.wilkie.cakes.cakelist.ui.CakeListUiState.Error
import dan.wilkie.cakes.cakelist.ui.CakeListUiState.Loading
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CakeListViewModel(private val repo: CakeListRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<CakeListUiState>(Loading)
    private val displayRefreshError = MutableStateFlow(false)
    private val isRefreshing = MutableStateFlow(false)

    val uiState: Flow<CakeListUiState> by lazy {
        initialLoad()
        combine(_uiState, isRefreshing, displayRefreshError) { data, refreshing, displayRefreshError ->
            when (data) {
                is Content -> data.copy(refreshing = refreshing, displayRefreshError = displayRefreshError)
                else -> data
            }
        }
    }

    fun initialLoad() {
        viewModelScope.launch {
            _uiState.emit(Loading)
            try {
                val result = repo.loadData()
                _uiState.emit(Content(result))
            } catch (throwable: Throwable) {
                _uiState.emit(Error)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing.emit(true)
            try {
                val result = repo.loadData(refresh = true)
                _uiState.emit(Content(result))
            } catch (throwable: Throwable) {
                displayRefreshError.emit(true)
            }
            isRefreshing.emit(false)
        }
    }

    fun refreshErrorShown() {
        viewModelScope.launch {
            displayRefreshError.emit(false)
        }
    }
}

sealed interface CakeListUiState {
    data object Loading : CakeListUiState
    data class Content(
        val cakes: List<Cake>,
        val refreshing: Boolean = false,
        val displayRefreshError: Boolean = false
    ) : CakeListUiState
    data object Error : CakeListUiState
}