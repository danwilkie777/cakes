package dan.wilkie.cakes.common.domain

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

open class Repository<T>(
    private val request: suspend () -> T
) {
    private val mutex = Mutex()
    private var data: T? = null

    suspend fun loadData(refresh: Boolean = false): T {
        val cachedData = data
        return if (refresh || cachedData == null) {
            val result = request()
            mutex.withLock {
                data = result
                result
            }
        } else {
            mutex.withLock { cachedData }
        }
    }
}

