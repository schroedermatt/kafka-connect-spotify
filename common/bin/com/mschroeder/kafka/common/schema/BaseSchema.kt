package com.mschroeder.kafka.common.schema

open class BaseSchema {
    companion object {
        // schema names
        const val SCHEMA_NAMESPACE  = "com.mschroeder.kafka.connect.spotify"

        // common fields
        const val ID_FIELD          = "id"
        const val NAME_FIELD        = "name"
        const val HREF_FIELD        = "href"
        const val URI_FIELD         = "uri"
        const val TYPE_FIELD        = "type" // album, artist, audio_features, genre, playlist, track, user
    }
}
