package com.mschroeder.kafka.connect.spotify.config

import org.apache.kafka.common.config.AbstractConfig
import org.apache.kafka.common.config.ConfigDef

// shell class to gain access to AbstractConfig prop retrieval helpers
class SpotifySourceConfig(definition: ConfigDef?, originals: MutableMap<*, *>?)
    : AbstractConfig(definition, originals)