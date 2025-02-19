## Overview
Service to shorten urls

## Technologies/Libraries used:

<ul>
  <li>Spring Boot</li>
  <li>Kotlin</li>
  <li>PostgreSQL</li>
  <li>Junit 5</li>
  <li>Testcontainers - to spin up postgres instance for Integrations tests</li>
</ul>

## Dependencies
- In order to start service locally you need to start a postgreSQL instance, you can do that by executing **"docker compose up postgres"**
- if you are on a older version of docker, you may have to use **"docker-compose up postgres"**
- This service is using gradle wrapper, it is not necessary to have gradle in the execution environment. Just use gradle commands with ./gradlew prefix.

## REST API
* `POST` /v1/url-shortner/shorten - shorten url
    - example request body
  ```json
    {
      "url": "url.com"
    }
  ```
* `GET` /v1/url-shortner/{shortCode}/expand - returns original url for a given shortcode
    - shortCode is part of POST **/v1/url-shortner/shorten** api response.
* `GET` /v1/url-shortner/{originalUrl}/stats - get statistics like visited count, created date and last accessed date for a given original url.

## How to start service locally in IDE directly using Main method
<ul>
  <li><b>docker compose up postgres</b> - to start postgres instance</li>
  <li>Now just run/debug main method inside of UrlShorteningServiceApplication.</li>
 </ul>

## How to start service locally using Docker
<ul>
  <li><b>./gradlew clean build</b> - create a executable jar file</li>
  <li><b>docker compose build --no-cache</b> - build/rebuild the docker image</li>
  <li><b>docker compose up</b> - starts the service locally (including postgres dependency)</li>
 </ul>

## Clean up docker containers
<ul>
  <li><b>docker compose down</b> - shuts down the running docker containers from this project</li>
</ul>

## Future improvements
- add alias name to /v1/url-shortner/shorten api request.
- add distribute caching
- implement rate limiting 
- add swagger
- Maybe change to Hash based base62 approach when generating short code for better security. Right now the project uses database id to base62 conversion approach, assuming we won't be storing any sensitive urls (usually sensitive urls are protected using login or some sort of authentication)
