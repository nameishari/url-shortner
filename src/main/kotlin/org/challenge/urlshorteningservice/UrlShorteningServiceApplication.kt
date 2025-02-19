package org.challenge.urlshorteningservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.retry.annotation.EnableRetry

@SpringBootApplication
@EnableRetry
@EnableJpaAuditing
class UrlShorteningServiceApplication

fun main(args: Array<String>) {
    runApplication<UrlShorteningServiceApplication>(*args)
}
