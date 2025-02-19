package org.challenge.urlshorteningservice.api.model

import jakarta.validation.constraints.NotBlank

data class UrlShortnerRequest(
    @NotBlank val url: String,
)