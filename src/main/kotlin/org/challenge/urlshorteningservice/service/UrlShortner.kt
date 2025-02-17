package org.challenge.urlshorteningservice.service

import org.sqids.Sqids

object UrlShortener {
    private val squids = Sqids.builder()
        .alphabet("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz")
        .minLength(5)
        .build()

    fun encode(id: Long): String = squids.encode(listOf(id))
}