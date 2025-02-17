package org.challenge.urlshorteningservice.repository

import org.challenge.urlshorteningservice.model.ShortUrl
import org.springframework.data.repository.CrudRepository

interface ShortUrlRepository : CrudRepository<ShortUrl, Long> {
    fun findByShortCode(shortCode: String): ShortUrl?
    fun findByOriginalUrl(originalUrl: String): ShortUrl?
}