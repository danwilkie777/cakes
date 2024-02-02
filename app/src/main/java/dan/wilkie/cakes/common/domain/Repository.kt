package dan.wilkie.cakes.common.domain

import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.*

open class Repository<T>(
    private val request: suspend () -> T
) {
    private val _data = MutableSharedFlow<T>(replay = 1, onBufferOverflow = DROP_OLDEST)
    val data: SharedFlow<T> = _data

    suspend fun initialLoad() {
        if (isEmpty()) loadData()
    }

    suspend fun refresh() {
        loadData()
    }

    private suspend fun loadData() {
        val result = request()
        _data.emit(result)
    }

    private fun isEmpty() = _data.replayCache.isEmpty()
}

