package org.challenge.urlshorteningservice.api.model

data class UrlShortnerResponse(
    val originalUrl: String,
    val shortCode: String,
)