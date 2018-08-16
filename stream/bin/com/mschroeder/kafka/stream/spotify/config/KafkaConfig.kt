package com.mschroeder.kafka.stream.spotify.config

import com.mschroeder.kafka.common.schema.PlayHistory
import io.confluent.connect.avro.AvroData
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroDeserializer
import org.apache.avro.Schema
import org.apache.avro.specific.SpecificDatumReader
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.connect.data.SchemaAndValue
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.processor.WallclockTimestampExtractor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.EnableKafkaStreams
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration
import java.util.HashMap


@Configuration
@EnableKafka
@EnableKafkaStreams
class KafkaConfig {
    /**
     * This config will be used as the default for ALL streams.
     * Streams should override individual props as needed.
     * @param kafkaProps kafka properties pulled from application.yaml
     * @return defaultKafkaStreamsConfig instance
     */
    @Bean(name = [(KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)])
    fun streamsConfig(kafkaProps: KafkaProperties): StreamsConfig {
        val props = HashMap<String, Any>()
        props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = props
        props[StreamsConfig.APPLICATION_ID_CONFIG] = kafkaProps.applicationId
        props[StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG] = WallclockTimestampExtractor::class.java.name

        props[AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG] = kafkaProps.schemaRegistryUrl
        // props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, ExceptionHandler.class);

        return StreamsConfig(props)
    }

    fun serde(): Serde<PlayHistory> {
        val avroData = AvroData(10000)
        val schema: Schema = avroData.fromConnectSchema(PlayHistory.SCHEMA)
        val reader = SpecificDatumReader<com.wrapper.spotify.model_objects.specification.PlayHistory>(schema)
    }
}