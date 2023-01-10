package dan.wilkie.cakes.common.network

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

class MockWebServerBuilder private constructor() {
    private val mockWebServer = MockWebServer()

    fun returningJson(json: String, statusCode: Int = 200): MockWebServerBuilder {
        val response = MockResponse()
            .setBody(json)
            .setResponseCode(statusCode)
            .setHeader(CONTENT_TYPE, APPLICATION_JSON)
        mockWebServer.enqueue(response)
        return this
    }

    fun start(): MockWebServer = mockWebServer.also { it.start() }

    companion object {
        private const val CONTENT_TYPE = "Content-Type"
        private const val APPLICATION_JSON = "application/json"
        fun aMockWebServer() = MockWebServerBuilder()
    }
}
