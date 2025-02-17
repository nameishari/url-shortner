package org.challenge.urlshorteningservice.util

import org.apache.commons.validator.routines.UrlValidator

fun String.isValidUrl(): Boolean {
    val urlToCheck = if (this.contains("://")) this else "http://$this"
    return UrlValidator().isValid(urlToCheck)
}