package dan.wilkie.cakes.cakelist.network

import dan.wilkie.cakes.cakelist.domain.Cake
import dan.wilkie.cakes.common.network.MockWebServerBuilder.Companion.aMockWebServer
import dan.wilkie.cakes.common.network.RetrofitFactory
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Test

class RetrofitCakeListServiceTest {

    @Test
    @Throws(InterruptedException::class)
    fun `loads cakes and maps into domain model`() {
        runBlocking {
            val mockServer: MockWebServer = aMockWebServer().returningJson(JSON).start()
            val service = RetrofitCakeListService(createApi(mockServer))
            val cakes = service.cakes()
            assertEquals(expected, cakes)
        }
    }

    @Test
    @Throws(InterruptedException::class)
    fun `sorts cakes and removes duplicates`() {
        runBlocking {
            val mockServer: MockWebServer = aMockWebServer()
                .returningJson(UNSORTED_JSON_WITH_DUPLICATES)
                .start()
            val service = RetrofitCakeListService(createApi(mockServer))
            val cakes = service.cakes()
            assertEquals(expected, cakes)
        }
    }

    private fun createApi(mockServer: MockWebServer): CakeApi {
        val retrofit = RetrofitFactory(mockServer.url("/").toString()).createRetrofit()
        return retrofit.create(CakeApi::class.java)
    }

    companion object {
        private val expected =
            listOf(
                Cake("Chocolate", "Old fashioned chocolate cake", "chocolate.jpg"),
                Cake("Lemon Drizzle", "A tangy option", "lemon_drizzle.jpg")
            )

        private const val JSON = """ 
        [
          {
            "title":"Chocolate",
            "desc":"Old fashioned chocolate cake",
            "image":"chocolate.jpg"
          },
          {
            "title":"Lemon Drizzle",
            "desc":"A tangy option",
            "image":"lemon_drizzle.jpg"
          }
        ]
        """

        private const val UNSORTED_JSON_WITH_DUPLICATES = """ 
        [
          {
            "title":"Lemon Drizzle",
            "desc":"A tangy option",
            "image":"lemon_drizzle.jpg"
          },
          {
            "title":"Chocolate",
            "desc":"Old fashioned chocolate cake",
            "image":"chocolate.jpg"
          },
          {
            "title":"Lemon Drizzle",
            "desc":"A tangy option",
            "image":"lemon_drizzle.jpg"
          }
        ]
        """
    }
}