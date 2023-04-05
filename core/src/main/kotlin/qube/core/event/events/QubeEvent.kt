package qube.core.event.events

import io.cloudevents.CloudEvent
import io.cloudevents.core.v1.CloudEventBuilder
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata
import org.eclipse.microprofile.reactive.messaging.Message
import org.eclipse.microprofile.reactive.messaging.Metadata
import qube.core.util.QubeObjectMapper
import java.net.URI
import java.util.UUID

interface QubeEvent {
    val eventSource: URI?

    fun toEventMessage(topic: String? = null): Message<CloudEvent> {
        val kafkaMetadata = OutgoingKafkaRecordMetadata.builder<Any>()
        topic?.let { kafkaMetadata.withTopic(it) }

        return Message.of(CloudEventBuilder()
            .withId("id-" + UUID.randomUUID().toString())
            .withSource(eventSource)
            .withType(javaClass.simpleName)
            .withData(QubeObjectMapper.json.writeValueAsBytes(this))
            .withDataContentType("application/json")
            .withDataSchema(URI.create("/qube/schema/${this.javaClass.simpleName}"))
            .build(), Metadata.of(kafkaMetadata.build()))
    }
}
