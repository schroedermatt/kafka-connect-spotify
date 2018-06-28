package com.mschroeder.kafka.connect.spotify

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.exceptions.SpotifyWebApiException
import com.wrapper.spotify.exceptions.detailed.BadRequestException
import com.wrapper.spotify.exceptions.detailed.UnauthorizedException
import com.wrapper.spotify.model_objects.specification.PagingCursorbased
import com.wrapper.spotify.model_objects.specification.PagingCursorbased.Builder
import com.wrapper.spotify.model_objects.specification.PlayHistory
import org.slf4j.LoggerFactory
import java.io.IOException

class SpotifyClient(oauthToken: String, clientId: String, clientSecret: String) {
    private val log = LoggerFactory.getLogger(SpotifyClient::class.java)
    private val api: SpotifyApi

    // todo: load user id in init()
    val currentUser = "123"

    companion object {
        private val EMPTY_RESPONSE = Builder<PlayHistory>()
                .setTotal(0)
                .setItems(arrayOf())
                .build()
    }

    init {
        val builder = SpotifyApi.Builder()

        if (clientId.isNotEmpty() && clientSecret.isNotEmpty()) {
            builder
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
        } else if (oauthToken.isNotEmpty()) {
            builder
                    .setAccessToken(oauthToken)
        } else {
            throw RuntimeException("Spotify credentials are required to run but none were provided.")
        }

        this.api = builder.build()
    }

    fun getRecentlyPlayedTracks(after: Number, limit: Int = 50): PagingCursorbased<PlayHistory> {
        log.info("Retrieving play history after $after")

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

            log.info("todo - refresh token!")
            // todo: implement refresh logic using client id and secret
            // https://github.com/thelinmichael/spotify-web-api-java/blob/master/examples/authorization/client_credentials/ClientCredentialsExample.java
        } catch (e: BadRequestException) {
            log.error("Bad Request - unable to call Spotify: ", e)
        } catch (e: SpotifyWebApiException) {
            log.error("Exception - unable to call Spotify: ", e)
        } catch (e: IOException) {
            log.error("IO Exception - unable to call Spotify: ", e)
        }

        return response
    }
}
