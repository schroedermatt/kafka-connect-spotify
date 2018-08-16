package com.mschroeder.kafka.common.schema

import org.apache.kafka.connect.data.Schema
import org.apache.kafka.connect.data.SchemaBuilder
import com.wrapper.spotify.model_objects.specification.Context as ContextModel

class Context : BaseSchema() {
    companion object {
        const val VERSION = 1
        const val LOGICAL_NAME = "$SCHEMA_NAMESPACE.Context"

        val SCHEMA: Schema
            get() {
                return SchemaBuilder.struct()
                        .name(LOGICAL_NAME)
                        .version(VERSION)
                        .field(HREF_FIELD, Schema.STRING_SCHEMA)
                        .field(URI_FIELD, Schema.STRING_SCHEMA)
                        .field(TYPE_FIELD, Schema.STRING_SCHEMA)
                        .optional()
                        .build()
            }
    }

}
