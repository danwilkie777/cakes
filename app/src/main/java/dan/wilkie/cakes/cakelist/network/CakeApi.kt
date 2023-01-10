package dan.wilkie.cakes.cakelist.network

import dan.wilkie.cakes.cakelist.domain.Cake
import retrofit2.http.GET

interface CakeApi {
    @GET("waracle_cake-android-client")
    suspend fun cakes(): List<CakeDto>
}

data class CakeDto(
    val title: String = "",
    val desc: String = "",
    val image: String = ""
) {
    fun toDomain() = Cake(title, desc, image)
}