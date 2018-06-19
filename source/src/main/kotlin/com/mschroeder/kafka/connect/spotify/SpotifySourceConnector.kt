package com.mschroeder.kafka.connect.spotify

import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.connect.connector.Task
import org.apache.kafka.connect.source.SourceConnector
import org.slf4j.LoggerFactory

class SpotifySourceConnector : SourceConnector() {
    private val log = LoggerFactory.getLogger(SpotifySourceConnector::class.java)

    // lateinit due to not having config until start()
    private lateinit var sourceConfig: SpotifySourceConfig

    override fun start(config: MutableMap<String, String>?) {
        log.info("starting SpotifySourceConnector v${version()}")

        sourceConfig = SpotifySourceConfig(Config.spotify, config)
    }

    override fun stop() {
        log.info("stopping SpotifySourceConnector")
    }

    override fun version(): String {
        return Config.VERSION
    }

    override fun taskClass(): Class<out Task>? {
        return SpotifyTask::class.java
    }

    override fun taskConfigs(maxTasks: Int): MutableList<MutableMap<String, String>> {
        // can return config for up to maxTasks, but only returning config for 1 task
        return mutableListOf(sourceConfig.originalsStrings())
    }

    override fun config(): ConfigDef {
        return Config.spotify
    }
}
