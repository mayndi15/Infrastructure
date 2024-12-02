package kafka.producer

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory

class KafkaProducers<K, V>(private val bootstrapServers: String) {

    private val logger = LoggerFactory.getLogger("Producer")

    private val producer: KafkaProducer<K, V> by lazy {
        KafkaProducer(KafkaProducerConfig.producer(bootstrapServers))
    }

    fun send(
        topic: String,
        key: K?,
        value: V,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        logger.info("Sending event to topic: $topic")

        val record = ProducerRecord(topic, key, value)
        producer.send(record) { _, ex ->
            if (ex != null) {
                logger.error("Error sending event: ${ex.message}", ex)
                onError(ex)
            } else {
                logger.info("Event sent - Topic: $topic")
                onSuccess()
            }
        }
    }

    fun close() {
        producer.flush()
        producer.close()
    }
}

