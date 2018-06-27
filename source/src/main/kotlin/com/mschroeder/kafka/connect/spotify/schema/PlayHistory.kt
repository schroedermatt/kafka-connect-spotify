package com.mschroeder.kafka.connect.spotify.schema

import org.apache.kafka.connect.data.Schema
import org.apache.kafka.connect.data.SchemaBuilder
import org.apache.kafka.connect.data.Timestamp
import com.wrapper.spotify.model_objects.specification.PlayHistory as PlayHistoryModel

class PlayHistory : BaseSchema() {
    companion object {
        const val VERSION = 1
        const val LOGICAL_NAME = "$SCHEMA_NAMESPACE.PlayHistory"

        // fields
        const val PLAYED_AT_FIELD   = "played_at"
        const val TRACK_FIELD       = "track"
        const val CONTEXT_FIELD     = "context"

        val SCHEMA: Schema
            get() {
                return SchemaBuilder.struct()
                        .name(LOGICAL_NAME)
                        .version(VERSION)
                        .field(PLAYED_AT_FIELD, Timestamp.SCHEMA)
                        .field(TRACK_FIELD, Track.SCHEMA)
                        .field(CONTEXT_FIELD, Context.SCHEMA)
                        .build()
            }
    }
}