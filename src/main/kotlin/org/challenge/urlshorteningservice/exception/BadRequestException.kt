package org.challenge.urlshorteningservice.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadRequestException(
    override val message: String
) : RuntimeException(message)