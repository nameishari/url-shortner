package org.challenge.urlshorteningservice.api

import com.fasterxml.jackson.databind.ObjectMapper
import org.challenge.urlshorteningservice.api.model.UrlShortnerRequest
import org.challenge.urlshorteningservice.model.ShortUrl
import org.challenge.urlshorteningservice.repository.ShortUrlRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.*
import org.challenge.urlshorteningservice.api.model.StatsResponse
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UrlShortenerControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var  repository: ShortUrlRepository
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        private val db = PostgreSQLContainer("postgres:15")

        @DynamicPropertySource
        @JvmStatic
        fun registerDBContainer(registry: DynamicPropertyRegistry) {
            db.start()
            registry.add("DB_URL", db::getJdbcUrl)
            registry.add("DB_USERNAME", db::getUsername)
            registry.add("DB_PASSWORD", db::getPassword)
        }
    }

    @Test
    fun `should shorten a URL and persist it in database`() {
        // Given
        val request = UrlShortnerRequest(url = "https://example.com")

        // When
        val response = mockMvc.post("/v1/url-shortner/shorten") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString

        val result = objectMapper.readValue<Map<String, String>>(response)
        val shortCode = result["shortCode"]

        // Then
        val savedUrl = repository.findByShortCode(shortCode!!)
        assertNotNull(savedUrl)
        assertEquals(request.url, savedUrl?.originalUrl)
    }

    @Test
    fun `should expand a short URL and update visit count`() {
        // Given
        val originalUrl = "https://test.com"
        val saved = repository.save(ShortUrl(originalUrl = originalUrl, shortCode = "abc123", visitCount = 0))

        // When
        val response = mockMvc.get("/v1/url-shortner/${saved.shortCode}/expand")
            .andExpect {
                status { isOk() }
            }.andReturn().response.contentAsString

        val result = objectMapper.readValue<Map<String, String>>(response)
        assertEquals(originalUrl, result["originalUrl"])

        // Then
        val updatedUrl = repository.findByShortCode(saved.shortCode!!)
        assertEquals(1, updatedUrl?.visitCount)
    }

    @Test
    fun `should return 404 when expanding a non-existent short code`() {
        mockMvc.get("/v1/url-shortner/nonexistent/expand")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `shorten api - should handle concurrent requests for same url`() {
        // Given
        val originalUrl = "https://samesame.com"

        runBlocking {
            val task1 = async(Dispatchers.IO) {
                mockMvc.post("/v1/url-shortner/shorten") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(UrlShortnerRequest(url = originalUrl))
                }.andExpect {
                    status { isOk() }
                }
            }
            val task2 = async {
                mockMvc.post("/v1/url-shortner/shorten") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(UrlShortnerRequest(url = originalUrl))
                }.andExpect {
                    status { isOk() }
                }
            }
            task1.await()
            task2.await()
        }

        assertNotNull(repository.findByOriginalUrl(originalUrl)?.shortCode)
    }

    @Test
    fun `get by shortcode - should handle concurrent requests for same short code`() {
        // Given
        val originalUrl = "https://testing.com"
        val saved = repository.save(ShortUrl(originalUrl = originalUrl, shortCode = "abc1235", visitCount = 0))

        runBlocking {
            val task1 = async(Dispatchers.IO) {
                mockMvc.get("/v1/url-shortner/${saved.shortCode}/expand")
                    .andExpect {
                        status { isOk() }
                    }
            }
            val task2 = async {
                mockMvc.get("/v1/url-shortner/${saved.shortCode}/expand")
                    .andExpect {
                        status { isOk() }
                    }
            }
            task1.await()
            task2.await()
        }

        assertEquals(2, repository.findByOriginalUrl(originalUrl)?.visitCount)
    }

    @Test
    fun `stats api - should return stats for an existing original URL`() {
        // Given
        val originalUrl = "https://another.com"
        val shortCode = "abc12398"
        val visitCount = 5

        repository.save(ShortUrl(
            originalUrl = originalUrl,
            shortCode = shortCode,
            visitCount = visitCount
        ))

        // When
        val response = mockMvc.get("/v1/url-shortner/stats") {
            param("originalUrl", originalUrl)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString

        // Then
        val statsResponse: StatsResponse = objectMapper.readValue(response)

        // Assert that the stats response contains the expected values
        assertEquals(originalUrl, statsResponse.originalUrl)
        assertEquals(shortCode, statsResponse.shortCode)
        assertEquals(visitCount, statsResponse.visitCount)
        assertNotNull(statsResponse.createdAt)
        assertNotNull(statsResponse.lastAccessedAt)
    }

    @Test
    fun `stats api - should return 404 when originalUrl does not exist`() {
        mockMvc.get("/v1/url-shortner/stats") {
            param("originalUrl", "https://nonexistenturl.com")
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }
    }

}
