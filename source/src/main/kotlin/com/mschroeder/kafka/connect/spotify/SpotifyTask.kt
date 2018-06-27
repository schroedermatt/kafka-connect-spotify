package com.mschroeder.kafka.connect.spotify

import com.mschroeder.kafka.connect.spotify.config.Config
import com.mschroeder.kafka.connect.spotify.config.SpotifySourceConfig
import com.mschroeder.kafka.connect.spotify.schema.PlayHistory
import com.mschroeder.kafka.connect.spotify.schema.createStruct
import org.apache.kafka.connect.data.Schema
import org.apache.kafka.connect.source.SourceRecord
import org.apache.kafka.connect.source.SourceTask
import org.slf4j.LoggerFactory
import java.util.*


class SpotifyTask : SourceTask() {
    private val log = LoggerFactory.getLogger(SpotifyTask::class.java)

    // lateinit due to not having config until start()
    private lateinit var taskConfig: SpotifySourceConfig
    private lateinit var client: SpotifyClient
    private lateinit var topic: String
    private lateinit var pollingInterval: Number
    private lateinit var offset: Date

    override fun start(config: MutableMap<String, String>?) {
        log.info("starting SpotifyTask v${version()}")

        // initialize task config
        taskConfig = SpotifySourceConfig(Config.spotify, config)
        topic = taskConfig.getString(Config.SPOTIFY_KAFKA_TOPIC_CONF)
        pollingInterval = taskConfig.getInt(Config.SPOTIFY_POLLING_INTERVAL_CONF)

        // start loading data from 6 months ago
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -6)
        offset = cal.time

        // setup client for Spotify
        val oauthToken = taskConfig.getPassword(Config.SPOTIFY_OAUTH_ACCESS_TOKEN_CONF).value()
        val clientId = taskConfig.getPassword(Config.SPOTIFY_OAUTH_CLIENT_ID_CONF).value()
        val clientSecret = taskConfig.getPassword(Config.SPOTIFY_OAUTH_CLIENT_SECRET_CONF).value()
        client = SpotifyClient(oauthToken, clientId, clientSecret)

        log.info("SpotifyTask is configured")
    }

    override fun stop() {
        log.info("stopping SpotifyTask")
    }

    override fun version(): String = Config.VERSION

    override fun poll(): MutableList<SourceRecord> {
        log.info("** polling **")

        val playHistory = client.getRecentlyPlayed(offset)

        val records = playHistory
                // update offset to most recently played track
                .onEach { updateOffset(it.playedAt) }
                // convert each item to a SourceRecord
                .map {
                    SourceRecord(
                            mutableMapOf("user_id" to client.currentUserId),
                            mutableMapOf("played_at" to it.playedAt.time),
                            topic,
                            // message key
                            Schema.STRING_SCHEMA,
                            it.track.id,
                            // message value
                            PlayHistory.SCHEMA,
                            it.createStruct()
                    )
                }

        return records.toMutableList()
    }

    /**
     * If track was played more recently than the current offset, update it
     */
    private fun updateOffset(playedAt: Date) {
        if (playedAt > offset) {
            offset = playedAt
        }
    }
}