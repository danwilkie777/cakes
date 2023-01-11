package dan.wilkie.cakes.cakelist.network

import dan.wilkie.cakes.cakelist.domain.Cake
import dan.wilkie.cakes.cakelist.domain.CakeListService

class RetrofitCakeListService(private val cakeApi: CakeApi): CakeListService {
    override suspend fun cakes(): List<Cake> {
        val cakes = cakeApi.cakes()
        return cakes
            .distinctBy { it.title }
            .sortedBy { it.title }
            .map { it.toDomain() }
    }
}