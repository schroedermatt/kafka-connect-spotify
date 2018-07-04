
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.KStreamBuilder
import java.util.*

fun main(args: Array<String>) {
    val bootstrapServers = if (args.isNotEmpty()) args[0] else "broker:9092"
    val schemaRegistryUrl = if (args.size > 1) args[1] else "schema-registry:8081"

    val streams: KafkaStreams = buildTopSongsStream(
            bootstrapServers,
            schemaRegistryUrl,
            "/tmp/kafka-streams"
    )

    // If args are empty the app is running locally, so we will always clean local start prior to processing.
    // The drawback of cleaning up local state prior is that your app must rebuild local state from scratch,
    // which takes time and requires reading all the state-relevant data from the Kafka cluster over the network.
    if (args.isEmpty()) {
        streams.cleanUp()
    }

    streams.start()

    // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
    Runtime
            .getRuntime()
            .addShutdownHook(Thread(Runnable { streams.close() }))

}

private fun buildTopSongsStream(bootstrapServers: String, schemaRegistryUrl: String, stateDir: String): KafkaStreams {
    val streamsConfiguration = Properties()

    streamsConfiguration[StreamsConfig.APPLICATION_ID_CONFIG] = "top-songs"
    streamsConfiguration[StreamsConfig.CLIENT_ID_CONFIG] = "top-songs-client"
    streamsConfiguration[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
    streamsConfiguration[AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG] = schemaRegistryUrl
    streamsConfiguration[StreamsConfig.STATE_DIR_CONFIG] = stateDir

    // Records should be flushed every 10 seconds.
    // This is less than the default in order to keep this example interactive.
    streamsConfiguration[StreamsConfig.COMMIT_INTERVAL_MS_CONFIG] = 10 * 1000

    val keySerde = Serdes.String()
    val valueSerde = Serdes.serdeFrom(KafkaAvroSerializer(), KafkaAvroDeserializer())

    // todo: look into `fromConnectData`
    // https://github.com/confluentinc/schema-registry/blob/master/avro-converter/src/main/java/io/confluent/connect/avro/AvroConverter.java

    val builder = KStreamBuilder()

    builder
            .stream(keySerde, valueSerde,"spotify_play_history")
            .foreach({ key, value ->
                println("key: $key")
                println("value: $value")
            })

    return KafkaStreams(builder, streamsConfiguration)
}