package org.challenge.urlshorteningservice.api

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.challenge.urlshorteningservice.api.model.StatsResponse
import org.challenge.urlshorteningservice.api.model.UrlShortnerResponse
import org.challenge.urlshorteningservice.api.model.UrlShortnerRequest
import org.challenge.urlshorteningservice.service.UrlShortnerService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/url-shortner")
class UrlShortenerController(private val service: UrlShortnerService) {
    @PostMapping("/shorten")
    fun shorten(@RequestBody @Valid request: UrlShortnerRequest): UrlShortnerResponse {
        return UrlShortnerResponse(request.url, service.shortenUrl(request.url))
    }

    @GetMapping("/{shortCode}/expand")
    fun expand(@PathVariable shortCode: String): UrlShortnerResponse {
        return UrlShortnerResponse(service.findOriginalUrlByShortCode(shortCode), shortCode)
    }

    @GetMapping("/stats")
    fun stats(@RequestParam @NotBlank originalUrl: String): StatsResponse {
        return service.getStats(originalUrl)
    }
}