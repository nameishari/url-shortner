package org.challenge.urlshorteningservice.api.model

import org.hibernate.validator.constraints.URL

data class UrlShortnerRequest(
    @URL val url: String,
)