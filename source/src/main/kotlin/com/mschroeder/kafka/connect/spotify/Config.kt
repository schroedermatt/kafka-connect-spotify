package com.mschroeder.kafka.connect.spotify

import org.apache.kafka.common.config.ConfigDef

class Config {
    companion object {
        const val VERSION = "0.0.1"

        const val SPOTIFY_OAUTH_ACCESS_TOKEN_CONF = "spotify.oauth.accessToken"
        const val SPOTIFY_KAFKA_TOPIC_CONF = "spotify.kafka.topic"

        val spotify: ConfigDef
            get() {
                return ConfigDef()
                        .define(
                                SPOTIFY_OAUTH_ACCESS_TOKEN_CONF,
                                ConfigDef.Type.PASSWORD,
                                ConfigDef.Importance.HIGH,
                                "Access token for Spotify API."
                        )
                        .define(
                                SPOTIFY_KAFKA_TOPIC_CONF,
                                ConfigDef.Type.STRING,
                                ConfigDef.Importance.HIGH,
                                "Topic name for Spotify messages."
                        )
            }
    }
}