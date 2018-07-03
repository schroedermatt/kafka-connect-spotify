package com.mschroeder.kafka.connect.spotify.client

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.exceptions.detailed.UnauthorizedException
import com.wrapper.spotify.model_objects.credentials.ClientCredentials
import com.wrapper.spotify.model_objects.specification.PagingCursorbased
import com.wrapper.spotify.model_objects.specification.PagingCursorbased.Builder
import com.wrapper.spotify.model_objects.specification.PlayHistory
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*


class SpotifyClient(oauthToken: String, clientId: String, clientSecret: String) {
    private val log = LoggerFactory.getLogger(SpotifyClient::class.java)

    private var tokenExpiration: LocalDateTime = LocalDateTime.now().plusMinutes(60L)
    private val api: SpotifyApi

    companion object {
        private val EMPTY_RESPONSE = Builder<PlayHistory>()
                .setTotal(0)
                .setItems(arrayOf())
                .build()

        private val DATE_FORMAT = SimpleDateFormat("MM-dd-yyyy HH:mm:ss")
    }

    init {
        DATE_FORMAT.timeZone = TimeZone.getTimeZone("US/Central")
    }

    init {
        val builder = SpotifyApi.Builder()

        // todo: support client credentials (see Issue #8)

        if (oauthToken.isNotEmpty()) {
            builder.setAccessToken(oauthToken)
        } else {
            throw RuntimeException("Spotify credentials are required to run but none were provided.")
        }

        this.api = builder.build()
    }

    fun getRecentlyPlayedTracks(after: Number, limit: Int = 50): PagingCursorbased<PlayHistory> {
        validateAccessToken()

        log.info("Retrieving play history after ${DATE_FORMAT.format(Date(after.toLong()))}")

        var response = EMPTY_RESPONSE
        try {
            response = api
                    .currentUsersRecentlyPlayedTracks
                    .setQueryParameter("after", after)
                    .limit(limit)
                    .build()
                    .execute()

            log.info("Request complete: ${response.items.size} items retrieved")
        } catch (e: UnauthorizedException) {
            log.error("Unauthorized - unable to call Spotify: ", e)
            throw RuntimeException("Unable to call Spotify - ${e.message}")

            // todo: refresh token and retry
        } catch (e: Exception) {
            log.error("Unable to call Spotify: ", e)
            throw RuntimeException("Unable to call Spotify - ${e.message}")
        }

        return response
    }

    private fun validateAccessToken() {
        val now = LocalDateTime.now()

        // todo: uncomment when refreshing the token is supported
        // refresh token if we are within 10 seconds of expiration
        //if (now.minusSeconds(10L) >= tokenExpiration) {
        //    refreshToken()
        //}

        log.info("Token expires in ${now.until(tokenExpiration, ChronoUnit.SECONDS)} seconds")
    }

    private fun refreshToken() {
        try {
            log.info("Refreshing access token for client: ${api.clientId}")

            val response: ClientCredentials = api
                    .clientCredentials()
                    .build()
                    .execute()

            log.info("Access Token expires in ${response.expiresIn / 60} minutes")
            tokenExpiration = LocalDateTime.now().plusSeconds(response.expiresIn.toLong())

            api.accessToken = response.accessToken
        } catch (e: Exception) {
            log.error("Unable to refresh access token: ", e)
            throw RuntimeException("Failed to refresh Spotify access token.")
        }
    }
}
