package com.mschroeder.kafka.connect.spotify

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.exceptions.SpotifyWebApiException
import com.wrapper.spotify.exceptions.detailed.BadRequestException
import com.wrapper.spotify.model_objects.specification.PlayHistory
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.*

class SpotifyClient(val oauthToken: String) {
    private val log = LoggerFactory.getLogger(SpotifyClient::class.java)

    private var api: SpotifyApi = SpotifyApi.Builder()
            .setAccessToken(oauthToken)
            .build()

    fun getRecentlyPlayed(after: Date, limit: Int = 50): MutableList<PlayHistory> {
        log.info("Retrieving play history after ${after.time}")

        var items = mutableListOf<PlayHistory>()

        try {
            val response = api
                    .currentUsersRecentlyPlayedTracks
                    // the built in after() method does not convert date to timestamp
                    .setQueryParameter("after", after.time)
                    .limit(limit)
                    .build()
                    .execute()

            log.info("Request complete: ${response.items.size} items retrieved")

            items = response.items.toMutableList()
        } catch (e: BadRequestException) {
            log.error("Bad Request - unable to call Spotify: ", e)
        } catch (e: SpotifyWebApiException) {
            log.error("Exception - unable to call Spotify: ", e)
        } catch (e: IOException) {
            log.error("IO Exception - unable to call Spotify: ", e)
        }

        return items
    }
}
