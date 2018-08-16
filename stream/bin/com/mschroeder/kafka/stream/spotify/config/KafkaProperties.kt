package com.mschroeder.kafka.stream.spotify.config;

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "kafka")
class KafkaProperties {
	lateinit var applicationId: String
	lateinit var bootstrapServers: String
	lateinit var schemaRegistryUrl: String
}