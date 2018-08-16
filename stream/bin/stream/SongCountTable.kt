package stream

import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.Consumed
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.state.KeyValueStore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import java.util.ArrayList

/**
 * Stream Description:
 *
 * More details on aggregating -
 * https://docs.confluent.io/current/streams/developer-guide/dsl-api.html#aggregating
 */

@Configuration
class SongCountTable {

    @Bean
    fun packageTransactionsKTable(streamsBuilder: StreamsBuilder): KTable<String, String> {

        val stream = streamsBuilder
                .stream(
                        KafkaTopics.INNERTRACK_PACKAGE_TRANSACTIONS.getTopicName(),
                        Consumed.with(Serdes.String(), incomingSerde)
                )

        return stream
                .groupByKey()
                .aggregate(
                        /* called when record key received for first time */
                        { PackageTransactions(ArrayList<E>()) },
                        /* adder called on all new records */
                        { aggKey, newValue, aggValue ->
                            aggValue.getTransactions().add(newValue)

                            aggValue
                        },
                        /* ktable config (/tmp/kafka-streams/{application_id}/0_0/rocksdb/{table_name}) */
                        Materialized.`as`<String, PackageTransactions, KeyValueStore<Bytes, ByteArray>>(KTABLE_NAME)
                                .withKeySerde(Serdes.String())
                                .withValueSerde(outgoingSerde)
                )
    }

    companion object {
        internal val KTABLE_NAME = "song_count"
    }
}
