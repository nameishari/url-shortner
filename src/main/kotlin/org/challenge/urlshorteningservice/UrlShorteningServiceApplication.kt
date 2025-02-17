package org.challenge.urlshorteningservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UrlShorteningServiceApplication

fun main(args: Array<String>) {
    runApplication<UrlShorteningServiceApplication>(*args)
}
