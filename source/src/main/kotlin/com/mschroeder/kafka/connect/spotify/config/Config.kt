package com.mschroeder.kafka.connect.spotify.config

import org.apache.kafka.common.config.ConfigDef

class Config {
    companion object {
        const val VERSION = "0.0.1"

        const val SPOTIFY_OAUTH_ACCESS_TOKEN_CONF = "spotify.oauth.accessToken"
        const val SPOTIFY_OAUTH_CLIENT_ID_CONF = "spotify.oauth.clientId"
        const val SPOTIFY_OAUTH_CLIENT_SECRET_CONF = "spotify.oauth.clientSecret"
        const val SPOTIFY_KAFKA_TOPIC_CONF = "spotify.kafka.topic"
        const val SPOTIFY_POLLING_INTERVAL_CONF = "spotify.pollingInterval"

        val spotify: ConfigDef
            get() {
                return ConfigDef()
                        .define(
                                SPOTIFY_OAUTH_ACCESS_TOKEN_CONF,
                                ConfigDef.Type.PASSWORD,
                                ConfigDef.Importance.MEDIUM,
                                "Access token for Spotify API (not needed if using client id & secret)."
                        )
                        .define(
                                SPOTIFY_OAUTH_CLIENT_ID_CONF,
                                ConfigDef.Type.PASSWORD,
                                ConfigDef.Importance.HIGH,
                                "Client ID for Spotify API."
                        )
                        .define(
                                SPOTIFY_OAUTH_CLIENT_SECRET_CONF,
                                ConfigDef.Type.PASSWORD,
                                ConfigDef.Importance.HIGH,
                                "Client Secret for Spotify API."
                        )
                        .define(
                                SPOTIFY_KAFKA_TOPIC_CONF,
                                ConfigDef.Type.STRING,
                                "spotify-play-history",
                                ConfigDef.Importance.HIGH,
                                "Topic name for Spotify messages."
                        )
                        .define(
                                SPOTIFY_POLLING_INTERVAL_CONF,
                                ConfigDef.Type.INT,
                                30000,
                                ConfigDef.Importance.LOW,
                                "Polling interval (milliseconds) for loading Spotify data."

                        )
            }
    }
}