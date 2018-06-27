package com.mschroeder.kafka.connect.spotify.schema

import org.apache.kafka.connect.data.Schema
import org.apache.kafka.connect.data.SchemaBuilder

class Track : BaseSchema() {
    companion object {
        const val VERSION = 1
        const val LOGICAL_NAME = "$SCHEMA_NAMESPACE.Track"

        // fields
        const val ARTISTS_FIELD     = "artists"
        const val DURATION_MS_FIELD = "duration_ms"
        const val EXPLICIT_FIELD    = "explicit"
        const val IS_PLAYABLE_FIELD = "is_playable"
        const val PREVIEW_URL_FIELD= "preview_url"
        const val TRACK_NUM_FIELD   = "track_number"

        val SCHEMA: Schema
            get() {
                return SchemaBuilder.struct()
                        .name(LOGICAL_NAME)
                        .version(VERSION)
                        .field(ID_FIELD,  Schema.INT32_SCHEMA)
                        .field(HREF_FIELD, Schema.STRING_SCHEMA)
                        .field(URI_FIELD, Schema.STRING_SCHEMA)
                        .field(NAME_FIELD, Schema.STRING_SCHEMA)
                        .field(TYPE_FIELD, Schema.STRING_SCHEMA)
                        .field(DURATION_MS_FIELD, Schema.INT32_SCHEMA)
                        .field(EXPLICIT_FIELD, Schema.BOOLEAN_SCHEMA)
                        .field(IS_PLAYABLE_FIELD, Schema.BOOLEAN_SCHEMA)
                        .field(PREVIEW_URL_FIELD, Schema.STRING_SCHEMA)
                        .field(TRACK_NUM_FIELD, Schema.INT16_SCHEMA)
                        // todo: support List of artists
                        .field(ARTISTS_FIELD, Artist.SCHEMA)
                        .build()
            }
    }

}
