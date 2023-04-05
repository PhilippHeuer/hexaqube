package qube.moderation.subscriber

import io.cloudevents.core.v1.CloudEventV1
import io.github.oshai.KotlinLogging
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.reactive.messaging.Incoming
import qube.core.event.events.QubeMessageEvent
import qube.core.event.extensions.toLogString
import qube.core.util.QubeObjectMapper

@ApplicationScoped
class ModerationSubscriber {

    @Incoming("message-moderate")
    fun consume(cloudEvent: CloudEventV1): Uni<Void> {
        logger.debug { "event received: ${cloudEvent.toLogString()}"}

        return Uni.createFrom().item(cloudEvent)
            .map { QubeObjectMapper.json.readValue(it.data?.toBytes() ?: ByteArray(0), QubeMessageEvent::class.java) }
            .onItem().invoke { event ->
                // TODO: implement moderation
            }
            .onItem().ignore().andContinueWithNull()
            .onFailure().invoke { ex: Throwable ->
                logger.error(ex) { "event processing failed: ${cloudEvent.toLogString()}. Error: ${ex.message}" }
            }
            .onItem().invoke { _ -> logger.info { "event processed successfully: ${cloudEvent.toLogString()}"} }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
