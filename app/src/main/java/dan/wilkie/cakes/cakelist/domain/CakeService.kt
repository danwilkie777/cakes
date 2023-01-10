package dan.wilkie.cakes.cakelist.domain

interface CakeService {
    suspend fun get(): List<Cake>
}
