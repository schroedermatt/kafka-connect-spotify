package com.mschroeder.kafka.connect.spotify

import com.mschroeder.kafka.connect.spotify.config.Config
import com.mschroeder.kafka.connect.spotify.config.SpotifySourceConfig
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

    override fun version(): String = Config.VERSION

    override fun taskClass(): Class<out Task>? = SpotifyPlayHistoryTask::class.java

    // can return config for up to maxTasks, but only returning config for 1 task
    override fun taskConfigs(maxTasks: Int): MutableList<MutableMap<String, String>> =
            mutableListOf(sourceConfig.originalsStrings())

    override fun config(): ConfigDef = Config.spotify
}
