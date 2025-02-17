package org.challenge.urlshorteningservice.api

import jakarta.validation.Valid
import org.challenge.urlshorteningservice.api.model.ShortCodeResponse
import org.challenge.urlshorteningservice.api.model.UrlShortnerRequest
import org.challenge.urlshorteningservice.service.UrlShortnerService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/url-shortner")
class UrlShortenerController(private val service: UrlShortnerService) {
    @PostMapping("/shorten")
    fun shorten(@RequestBody @Valid request: UrlShortnerRequest): ShortCodeResponse {
        return ShortCodeResponse(request.url, service.shortenUrl(request.url))
    }

    @GetMapping("/{shortCode}/expand")
    fun expand(@PathVariable shortCode: String): ShortCodeResponse {
        return ShortCodeResponse(service.findOriginalUrlByShortCode(shortCode), shortCode)
    }
}