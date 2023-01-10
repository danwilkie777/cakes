package dan.wilkie.cakes.cakelist.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dan.wilkie.cakes.common.domain.Lce
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class CakeListViewModel(
    private val repo: CakeListRepository
): ViewModel() {
    private val _refreshErrors = MutableSharedFlow<Throwable>()
    val refreshErrors: SharedFlow<Throwable> = _refreshErrors
    val data: Flow<Lce<List<Cake>>> get() = repo.data

    fun refresh() {
        viewModelScope.launch {
            try {
                repo.refresh()
            } catch (throwable: Throwable) {
                _refreshErrors.emit(throwable)
            }
        }
    }
}