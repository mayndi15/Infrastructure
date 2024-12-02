package kafka.producer

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kafka.event.Event
import org.apache.kafka.common.serialization.Serializer

class JsonSerializer<T> : Serializer<T> {
    private val gson: Gson =
        GsonBuilder().registerTypeAdapter(Event::class.java, Event.EventAdapter()).create();

    override fun serialize(topic: String, data: T?): ByteArray {
        return gson.toJson(data).toByteArray(Charsets.UTF_8)
    }
}