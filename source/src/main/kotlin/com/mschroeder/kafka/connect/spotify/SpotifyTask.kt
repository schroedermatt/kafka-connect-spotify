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

    override fun version(): String {
        return Config.VERSION
    }

    override fun poll(): MutableList<SourceRecord> {
        log.info("** polling **")

        val records = mutableListOf<SourceRecord>()

        // todo
        val data = client.fetchMockData()
        val sourcePartition = mutableMapOf("user" to client.oauthToken)
        val sourceOffset = mutableMapOf("position" to "1")
        val schema = Schema.STRING_SCHEMA

        records += SourceRecord(sourcePartition, sourceOffset, topic, schema, data)

        return records
    }
}