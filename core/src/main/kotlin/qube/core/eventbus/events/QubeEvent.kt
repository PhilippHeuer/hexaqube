package qube.core.eventbus.events

import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import io.cloudevents.CloudEvent
import io.cloudevents.core.v1.CloudEventBuilder
import java.net.URI
import java.util.UUID

private val mapper = jacksonMapperBuilder().findAndAddModules().build()

interface QubeEvent {
    val eventSource: URI
    val eventType: String

    fun toCloudEvent(): CloudEvent {
        return CloudEventBuilder()
            .withId("id-" + UUID.randomUUID().toString())
            .withSource(eventSource)
            .withType(eventType)
            .withData(mapper.writeValueAsBytes(this))
            .withDataContentType("application/json")
            .withDataSchema(URI.create("/qube/schema/${this.javaClass.simpleName}"))
            .build()
    }
}
