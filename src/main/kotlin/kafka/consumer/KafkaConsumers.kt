package kafka.consumer

import kafka.producer.KafkaProducerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import java.time.Duration

class KafkaConsumers<K, V : Any>(
    private val bootstrapServers: String,
    private val groupId: String,
    private val topic: String,
) {
    private val consumer: KafkaConsumer<K, V> by lazy {
        KafkaConsumer(KafkaConsumerConfig.consumer(bootstrapServers, groupId))
    }
    private val producer: KafkaProducer<String, Any> by lazy {
        KafkaProducer(KafkaProducerConfig.producer(bootstrapServers))
    }

    fun consumer(process: (record: ConsumerRecord<K, V>) -> Unit) {
        consumer.subscribe(listOf(topic))
        val logger = LoggerFactory.getLogger("Consumer")

        logger.info("Consuming events from topic: $topic")

        while (true) {
            val records: ConsumerRecords<K, V> = consumer.poll(Duration.ofMillis(100))
            for (record in records) {
                try {
                    process(record)
                    logger.info("Event processed - Topic: ${record.topic()}, Partition: ${record.partition()}, Offset: ${record.offset()}")
                } catch (ex: Exception) {
                    logger.error("Error processing event: ${ex.message}", ex)
                    sendDLQ(
                        producer = producer,
                        topic = record.topic(),
                        key = record.key()?.toString(),
                        value = record.value(),
                        exception = ex
                    )
                }
            }
        }
    }

    fun close() {
        consumer.close()
        producer.flush()
        producer.close()
    }

    private fun sendDLQ(
        producer: KafkaProducer<String, Any>,
        topic: String,
        key: String?,
        value: Any,
        exception: Exception
    ) {
        val logger = LoggerFactory.getLogger("DLQ")

        try {
            val message = mapOf(
                "original_topic" to topic,
                "key" to key,
                "value" to value,
                "error" to exception.message,
                "stack_trace" to exception.stackTraceToString()
            )

            val record = ProducerRecord<String, Any>("DLQ_TOPIC", key, message)

            producer.send(record) { metadata, ex ->
                if (ex != null) {
                    logger.error("Error sending event to DLQ: ${ex.message}", ex)
                } else {
                    logger.info(
                        "Event sent to DLQ - Topic: ${metadata.topic()}, Partition: ${metadata.partition()}, Offset: ${metadata.offset()}"
                    )
                }
            }
        } catch (e: Exception) {
            logger.error("Failed to process event for DLQ: ${e.message}", e)
        }
    }
}
