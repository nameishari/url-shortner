package org.challenge.urlshorteningservice.api.model

import java.time.Instant

data class StatsResponse(
    val createdAt: Instant,
    val lastAccessedAt: Instant,
    val visitCount: Int,
    val originalUrl: String,
    val shortCode: String
)