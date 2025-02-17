package org.challenge.urlshorteningservice.api.model

data class ShortCodeResponse(
    val originalUrl: String,
    val shortCode: String,
)