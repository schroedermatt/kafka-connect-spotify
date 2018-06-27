package com.mschroeder.kafka.connect.spotify.schema

import org.apache.kafka.connect.data.Schema
import org.apache.kafka.connect.data.SchemaBuilder
import com.wrapper.spotify.model_objects.specification.Artist as ArtistModel

class Artist : BaseSchema() {
    companion object {
        const val VERSION = 1
        const val LOGICAL_NAME = "$SCHEMA_NAMESPACE.Artist"

        val SCHEMA: Schema
            get() {
                return SchemaBuilder.struct()
                        .name(LOGICAL_NAME)
                        .version(VERSION)
                        .field(ID_FIELD,  Schema.STRING_SCHEMA)
                        .field(HREF_FIELD, Schema.STRING_SCHEMA)
                        .field(URI_FIELD, Schema.STRING_SCHEMA)
                        .field(NAME_FIELD, Schema.STRING_SCHEMA)
                        .field(TYPE_FIELD, Schema.STRING_SCHEMA)
                        .build()
            }
    }
}
