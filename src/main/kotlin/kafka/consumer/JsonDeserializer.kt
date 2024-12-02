package kafka.consumer

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kafka.event.Event
import org.apache.kafka.common.serialization.Deserializer

class JsonDeserializer<T>(private val clazz: Class<T>) : Deserializer<T> {
    private val gson: Gson =
        GsonBuilder().registerTypeAdapter(Event::class.java, Event.EventAdapter()).create();

    override fun deserialize(topic: String, data: ByteArray?): EventD? {
            return if (data == null) null else gson.fromJson(String(data, Charsets.UTF_8),  Event::class.java)
    }
}