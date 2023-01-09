package dan.wilkie.cakes.common.domain

import dan.wilkie.cakes.common.domain.Lce.*
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.*

class Repository<T>(
    private val request: suspend () -> T
) {
    private val _data = MutableSharedFlow<Lce<T>>(replay = 1, onBufferOverflow = DROP_OLDEST)
    val data: Flow<Lce<T>> = _data
        .onSubscription {
            loadInitialDataIfAbsent()
        }

    suspend fun refresh() {
        val result = request()
        _data.emit(Content(result))
    }

    private suspend fun loadInitialDataIfAbsent() {
        if (!hasContent()) {
            _data.emit(Loading)
            try {
                refresh()
            } catch (throwable: Throwable) {
                _data.emit(Error(throwable))
            }
        }
    }

    private fun hasContent() =
        _data.replayCache.isNotEmpty() && _data.replayCache.first() is Content<T>
}

sealed class Lce<out T> {
    object Loading : Lce<Nothing>()
    data class Content<T>(val value: T) : Lce<T>()
    data class Error<T>(val value: Throwable) : Lce<T>()
}