package com.mschroeder.kafka.connect.spotify

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
    private lateinit var offset: Date

    override fun start(config: MutableMap<String, String>?) {
        log.info("starting SpotifyTask v${version()}")

        // initialize config
        taskConfig = SpotifySourceConfig(Config.spotify, config)
        topic = taskConfig.getString(Config.SPOTIFY_KAFKA_TOPIC_CONF)

        // start loading data from 6 months ago
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -6)
        offset = cal.time

        // setup client for Spotify
        val oauthToken = taskConfig.getPassword(Config.SPOTIFY_OAUTH_ACCESS_TOKEN_CONF).value()
        client = SpotifyClient(oauthToken)
    }

    override fun stop() {
        log.info("stopping SpotifyTask")
    }

    override fun version(): String = Config.VERSION

    override fun poll(): MutableList<SourceRecord> {
        log.info("** polling **")

        val playHistory = client.getRecentlyPlayed(offset)
        val sourcePartition = mutableMapOf("user" to client.oauthToken)

        // todo: configure spotify PlayHistory schema
        val schema = Schema.STRING_SCHEMA

        val records = playHistory
                // update offset to most recently played track
                .onEach { updateOffset(it.playedAt) }
                // convert each item to a SourceRecord
                .map {
                    SourceRecord(
                            sourcePartition,
                            mutableMapOf("position" to it.playedAt.time),
                            topic,
                            schema,
                            it.track.id,
                            schema,
                            "${it.track.artists.first().name}: ${it.track.name}"
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