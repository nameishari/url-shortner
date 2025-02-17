package org.challenge.urlshorteningservice.config

import org.testcontainers.containers.PostgreSQLContainer

class PostgresContainer : PostgreSQLContainer<PostgresContainer>("postgres:15") {
    init {
        this.start()
        System.setProperty("DB_URL", this.jdbcUrl)
        System.setProperty("DB_USERNAME", this.username)
        System.setProperty("DB_PASSWORD", this.password)
    }
}