package org.challenge.urlshorteningservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry

@SpringBootApplication
@EnableRetry
class UrlShorteningServiceApplication

fun main(args: Array<String>) {
    runApplication<UrlShorteningServiceApplication>(*args)
}
