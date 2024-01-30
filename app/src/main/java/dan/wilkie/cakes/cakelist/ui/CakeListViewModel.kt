package dan.wilkie.cakes.cakelist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dan.wilkie.cakes.cakelist.domain.Cake
import dan.wilkie.cakes.cakelist.domain.CakeListRepository
import dan.wilkie.cakes.cakelist.ui.Event.REFRESH_FAILED
import dan.wilkie.cakes.common.domain.Lce
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CakeListViewModel(private val repo: CakeListRepository) : ViewModel() {
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events

    val screenState: Flow<Lce<List<Cake>>> get() = repo.data

    fun refresh() {
        viewModelScope.launch {
            try {
                repo.refresh()
            } catch (throwable: Throwable) {
                _events.emit(REFRESH_FAILED)
            }
            _isRefreshing.emit(false)
        }
    }

    fun retry() {
        viewModelScope.launch {
            repo.retry()
        }
    }
}

enum class Event {
    REFRESH_FAILED,
    NONE // TODO temp
}