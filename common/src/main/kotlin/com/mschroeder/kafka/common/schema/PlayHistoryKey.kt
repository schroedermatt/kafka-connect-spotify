package com.mschroeder.kafka.common.schema

import org.apache.kafka.connect.data.Schema
import org.apache.kafka.connect.data.SchemaBuilder
import com.wrapper.spotify.model_objects.specification.Context as ContextModel

class PlayHistoryKey : BaseSchema() {
    companion object {
        const val VERSION = 1
        const val LOGICAL_NAME = "$SCHEMA_NAMESPACE.PlayHistoryKey"

        // fields
        const val TRACK_ID_FIELD = "track_id"
        const val USERNAME_FIELD = "username"

        val SCHEMA: Schema
            get() {
                return SchemaBuilder.struct()
                        .name(LOGICAL_NAME)
                        .version(VERSION)
                        .field(TRACK_ID_FIELD, Schema.STRING_SCHEMA)
                        .field(USERNAME_FIELD, Schema.STRING_SCHEMA)
                        .build()
            }
    }

}
