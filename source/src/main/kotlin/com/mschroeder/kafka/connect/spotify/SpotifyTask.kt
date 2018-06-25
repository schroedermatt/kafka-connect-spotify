package com.mschroeder.kafka.connect.spotify

import org.apache.kafka.connect.data.Schema
import org.apache.kafka.connect.source.SourceRecord
import org.apache.kafka.connect.source.SourceTask
import org.slf4j.LoggerFactory

class SpotifyTask : SourceTask() {
    private val log = LoggerFactory.getLogger(SpotifyTask::class.java)

    // lateinit due to not having config until start()
    private lateinit var taskConfig: SpotifySourceConfig
    private lateinit var client: SpotifyClient
    private lateinit var topic: String

    override fun start(config: MutableMap<String, String>?) {
        log.info("starting SpotifyTask v${version()}")

        // initialize config
        taskConfig = SpotifySourceConfig(Config.spotify, config)
        topic = taskConfig.getString(Config.SPOTIFY_KAFKA_TOPIC_CONF)

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

        // todo: send offset to getRecentlyPlayed (it currently always loads the same set of songs)
        val playHistory = client.getRecentlyPlayed()
        val sourcePartition = mutableMapOf("user" to client.oauthToken)
        val schema = Schema.STRING_SCHEMA

        // todo: convert to stream/map
        // generate SourceRecord for each PlayHistory item
        val records = mutableListOf<SourceRecord>()
        playHistory.forEach {
            val sourceOffset = mutableMapOf("position" to it.playedAt)
            records += SourceRecord(
                    sourcePartition,
                    sourceOffset,
                    topic,
                    schema,
                    it.track.id,
                    schema,
                    it.track.name
            )
        }

        return records
    }
}