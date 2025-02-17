package org.challenge.urlshorteningservice.service

import jakarta.transaction.Transactional
import org.challenge.urlshorteningservice.exception.BadRequestException
import org.challenge.urlshorteningservice.exception.NotFoundException
import org.challenge.urlshorteningservice.model.ShortUrl
import org.challenge.urlshorteningservice.repository.ShortUrlRepository
import org.challenge.urlshorteningservice.util.isValidUrl
import org.springframework.stereotype.Service

@Service
class UrlShortnerService(private val repository: ShortUrlRepository) {

    @Transactional
    fun shortenUrl(originalUrl: String): String {
        if (!originalUrl.isValidUrl()) {
            throw BadRequestException("Invalid URL")
        }
        val existingRecord = repository.findByOriginalUrl(originalUrl)
        if (existingRecord != null) return existingRecord.shortCode!!

        val savedUrl = repository.save(ShortUrl(originalUrl = originalUrl, shortCode = null))
        val shortCode = UrlShortener.encode(savedUrl.id!!)

        val updatedUrl = savedUrl.copy(shortCode = shortCode)
        repository.save(updatedUrl)
        return shortCode
    }

    fun findOriginalUrlByShortCode(shortCode: String): String {
        return repository.findByShortCode(shortCode)?.originalUrl ?: throw NotFoundException("No url exists")
    }

}