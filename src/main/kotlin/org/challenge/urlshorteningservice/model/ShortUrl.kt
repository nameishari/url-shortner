package org.challenge.urlshorteningservice.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "short_urls")
@EntityListeners(AuditingEntityListener::class)
data class ShortUrl (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "serial")
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val originalUrl: String,

    @Column(unique = true)
    val shortCode: String?,

    @Version
    var version: Int = 0,

    @Column(nullable = false)
    var visitCount: Int = 0,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: Instant? = null,

    @LastModifiedDate
    @Column(nullable = false)
    var lastModifiedAt: Instant? = null
)