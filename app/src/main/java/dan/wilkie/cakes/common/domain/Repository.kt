package dan.wilkie.cakes.common.domain

import dan.wilkie.cakes.common.domain.Lce.*
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.*

open class Repository<T>(
    private val request: suspend () -> T
) {
    private val _data = MutableStateFlow<Lce<T>>(Loading)
    val data: Flow<Lce<T>> = _data.onSubscription { if (isEmpty()) loadInitialData() }

    suspend fun refresh() {
        loadData()
    }

    suspend fun retry() {
        loadInitialData()
    }

    private suspend fun loadData() {
        val result = request()
        _data.emit(Content(result))
    }

    private suspend fun loadInitialData() {
        _data.emit(Loading)
        try {
            loadData()
        } catch (throwable: Throwable) {
            _data.emit(Error)
        }
    }

    private fun isEmpty() = _data.replayCache.firstOrNull() !is Content<T>
}

sealed interface Lce<out T> {
    data object Loading : Lce<Nothing>
    data class Content<T>(val value: T) : Lce<T>
    data object Error : Lce<Nothing>
}