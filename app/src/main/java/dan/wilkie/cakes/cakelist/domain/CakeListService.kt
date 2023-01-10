package dan.wilkie.cakes.cakelist.domain

interface CakeListService {
    suspend fun cakes(): List<Cake>
}
