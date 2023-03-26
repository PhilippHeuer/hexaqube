package qube.core.eventbus

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.cloudevents.CloudEvent
import io.cloudevents.core.v1.CloudEventBuilder
import io.github.oshai.KotlinLogging
import org.eclipse.microprofile.reactive.messaging.*
import qube.core.eventbus.domain.QubeMessageActionType
import qube.core.eventbus.events.QubeMessageEvent
import java.net.URI
import java.util.UUID
import javax.enterprise.context.ApplicationScoped

private val logger = KotlinLogging.logger {}
private val mapper = jacksonObjectMapper()

@ApplicationScoped
class QubeEventBus(
    @Channel("out-message-createupdate")
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 500)
    private var messageCreateUpdateEmitter: Emitter<CloudEvent>,

    @Channel("out-message-create")
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 500)
    private var messageCreateEmitter: Emitter<CloudEvent>,

    @Channel("out-message-update")
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 500)
    private var messageUpdateEmitter: Emitter<CloudEvent>,
) {
    fun publish(payload: QubeMessageEvent) {
        val event = CloudEventBuilder()
            .withId("id-"+ UUID.randomUUID().toString())
            .withSource(payload.source)
            .withType("qube.message")
            .withData(mapper.writeValueAsBytes(payload))
            .withDataContentType("application/json")
            .withDataSchema(URI.create("/qube/schema/${payload.javaClass.simpleName}"))
            .build()

        messageCreateUpdateEmitter.send(event)
        when(payload.action) {
            QubeMessageActionType.CREATE -> messageCreateEmitter.send(event)
            QubeMessageActionType.UPDATE -> messageUpdateEmitter.send(event)
            else -> {}
        }
    }
}
