package org.challenge.urlshorteningservice.service

import org.challenge.urlshorteningservice.exception.BadRequestException
import org.challenge.urlshorteningservice.exception.NotFoundException
import org.challenge.urlshorteningservice.model.ShortUrl
import org.challenge.urlshorteningservice.repository.ShortUrlRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class UrlShortenerServiceTest {

    @Mock
    private lateinit var repository: ShortUrlRepository

    private lateinit var underTest: UrlShortnerService

    @BeforeEach
    fun setUp() {
        underTest = UrlShortnerService(repository)
    }

    @Test
    fun `should shorten a new URL`() {
        // Given
        val originalUrl = "https://google.com"

        `when`(repository.findByOriginalUrl(originalUrl)).thenReturn(null)
        `when`(repository.save(any(ShortUrl::class.java))).thenReturn(ShortUrl(1, originalUrl, null))

        // When
        val result = underTest.shortenUrl(originalUrl)

        // Then
        assertEquals("5CAHF", result)
        verify(repository, times(2)).save(any(ShortUrl::class.java))
    }

    @Test
    fun `should return existing short code if URL already shortened`() {
        // Given
        val originalUrl = "https://google.com"
        val shortCode = "existingShortCode"
        val existingShortUrl = ShortUrl(originalUrl = originalUrl, shortCode = shortCode)

        `when`(repository.findByOriginalUrl(originalUrl)).thenReturn(existingShortUrl)

        // When
        val result = underTest.shortenUrl(originalUrl)

        // Then
        assertEquals(shortCode, result)
        verify(repository, never()).save(any(ShortUrl::class.java))
    }

    @Test
    fun `should throw BadRequestException when URL is invalid`() {
        // Given
        val invalidUrl = "ga"

        // when & then
        assertFailsWith<BadRequestException> {
            underTest.shortenUrl(invalidUrl)
        }
    }

    @Test
    fun `should return original URL when short code is found`() {
        // Given
        val shortCode = "abc123"
        val originalUrl = "https://google.com"
        val shortUrl = ShortUrl(originalUrl = originalUrl, shortCode = shortCode)

        `when`(repository.findByShortCode(shortCode)).thenReturn(shortUrl)

        // When
        val result = underTest.findOriginalUrlByShortCode(shortCode)

        // Then
        assertEquals(originalUrl, result)
    }

    @Test
    fun `should throw NotFoundException when short code is not found`() {
        // Given
        val shortCode = "nonExistingCode"

        `when`(repository.findByShortCode(shortCode)).thenReturn(null)

        // When and Then
        assertFailsWith<NotFoundException> {
            underTest.findOriginalUrlByShortCode(shortCode)
        }
    }
}
