package org.challenge.urlshorteningservice.service

import jakarta.transaction.Transactional
import org.challenge.urlshorteningservice.api.model.StatsResponse
import org.challenge.urlshorteningservice.exception.BadRequestException
import org.challenge.urlshorteningservice.exception.NotFoundException
import org.challenge.urlshorteningservice.model.ShortUrl
import org.challenge.urlshorteningservice.repository.ShortUrlRepository
import org.challenge.urlshorteningservice.util.isValidUrl
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.orm.ObjectOptimisticLockingFailureException

@Service
class UrlShortnerService(private val repository: ShortUrlRepository) {
    private val logger: Logger = LoggerFactory.getLogger(UrlShortnerService::class.java)

    @Transactional
    @Retryable(
        retryFor = [DataIntegrityViolationException::class, ObjectOptimisticLockingFailureException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000)
    )
    fun shortenUrl(originalUrl: String): String {
        if (!originalUrl.isValidUrl()) {
            throw BadRequestException("Invalid URL")
        }
        logger.info("Shortening URL: $originalUrl")
        val existingRecord = repository.findByOriginalUrl(originalUrl)
        if (existingRecord != null) {
            logger.info("URL $originalUrl already shortened, returning existing code")
            return existingRecord.shortCode!!
        }

        val savedUrl = repository.save(ShortUrl(originalUrl = originalUrl, shortCode = null))
        val shortCode = UrlShortener.encode(savedUrl.id!!)
        val updatedUrl = savedUrl.copy(shortCode = shortCode)
        repository.save(updatedUrl)
        logger.info("Shortened URL: $originalUrl")
        return shortCode
    }

    @Transactional
    @Retryable(
        value = [ObjectOptimisticLockingFailureException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 500)
    )
    fun findOriginalUrlByShortCode(shortCode: String): String {
        val shortUrl = repository.findByShortCode(shortCode)
            ?: throw NotFoundException("No URL exists for shortcode: $shortCode")

        shortUrl.visitCount += 1
        repository.save(shortUrl)
        logger.info("visited ${shortUrl.originalUrl}")
        return shortUrl.originalUrl
    }

    fun getStats(originalUrl: String): StatsResponse {
        val shortUrl = repository.findByOriginalUrl(originalUrl) ?: throw NotFoundException("No URL exists for originalUrl: $originalUrl")
        return StatsResponse(
            visitCount = shortUrl.visitCount,
            lastAccessedAt = shortUrl.lastModifiedAt!!,
            createdAt = shortUrl.createdAt!!,
            shortCode = shortUrl.shortCode!!,
            originalUrl = originalUrl
        )
    }

}