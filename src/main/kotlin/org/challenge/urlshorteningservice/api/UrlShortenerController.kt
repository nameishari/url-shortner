package org.challenge.urlshorteningservice.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1")
class UrlShortenerController {
    @GetMapping
    fun shorten(shortUrl: String): String {
        return "h"
    }
}