package kafka.event

import com.google.gson.*
import java.lang.reflect.Type
import java.util.UUID

object Event {

    fun correlationId(title: String): String {
        return "$title(${UUID.randomUUID()})"
    }

    fun withCorrelationId(id: String, title: String): String {
        return "$id-$title"
    }

    class EventAdapter : JsonSerializer<EventData<*>>, JsonDeserializer<EventData<*>> {

        override fun serialize(
            message: EventData<*>,
            type: Type,
            context: JsonSerializationContext
        ): JsonElement {
            val obj = JsonObject().apply {
                addProperty("type", message.payload!!::class.java.name)
                add("payload", context.serialize(message.payload))
                addProperty("correlationId", message.id)
            }
            return obj
        }

        override fun deserialize(
            jsonElement: JsonElement,
            type: Type,
            context: JsonDeserializationContext
        ): EventData<*> {
            val obj = jsonElement.asJsonObject
            val clazz = obj["type"].asString
            val correlationId = obj["correlationId"].asString
            return try {
                val payload = context.deserialize<Any>(
                    obj["payload"], Class.forName(clazz)
                )
                EventData(correlationId, payload)
            } catch (ex: ClassNotFoundException) {
                throw JsonParseException(ex)
            }
        }
    }

    data class EventData<T>(
        val id: String,
        val payload: T
    ) {
        override fun toString(): String {
            return "EventData(id='$id', payload=$payload)"
        }
    }
}
