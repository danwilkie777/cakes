package dan.wilkie.cakes.cakelist.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dan.wilkie.cakes.cakelist.domain.RefreshState.*
import dan.wilkie.cakes.common.domain.Lce
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CakeListViewModel(
    private val repo: CakeListRepository
) : ViewModel() {
    private val _refreshState = MutableStateFlow(IDLE)
    val refreshState: StateFlow<RefreshState> = _refreshState
    val screenState: Flow<Lce<List<Cake>>> get() = repo.data

    fun refresh() {
        viewModelScope.launch {
            _refreshState.emit(REFRESHING)
            try {
                repo.refresh()
            } catch (throwable: Throwable) {
                _refreshState.emit(FAILED)
                delay(50) // TODO Snackbar doesn't appear without this - needs more investigation
            }
            _refreshState.emit(IDLE)
        }
    }

    fun retry() {
        viewModelScope.launch {
            repo.retry()
        }
    }
}

enum class RefreshState {
    IDLE,
    REFRESHING,
    FAILED
}