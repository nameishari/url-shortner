package org.challenge.urlshorteningservice

import org.challenge.urlshorteningservice.config.PostgresContainer
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
@ActiveProfiles("integration-test")
class UrlShorteningServiceApplicationTests {

    @Container
    val postgreSQLContainer = PostgresContainer()

    @Test
    fun contextLoads() {
    }

}
