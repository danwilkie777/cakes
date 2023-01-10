@file:OptIn(ExperimentalCoroutinesApi::class)

package dan.wilkie.cakes.common.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher

fun <T> Flow<T>.collectInList(): List<T> {
    val emittedItems = mutableListOf<T>()
    CoroutineScope(UnconfinedTestDispatcher()).launch { toList(emittedItems) }
    return emittedItems
}