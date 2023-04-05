package qube.moderation.subscriber

import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.fasterxml.jackson.module.kotlin.readValue
import io.cloudevents.core.v1.CloudEventV1
import io.github.oshai.KotlinLogging
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.faulttolerance.Retry
import org.eclipse.microprofile.reactive.messaging.Incoming
import qube.core.eventbus.events.QubeMessageEvent
import qube.core.eventbus.extensions.toLogString
import qube.core.exception.EventException

private val logger = KotlinLogging.logger {}
private val mapper = jacksonMapperBuilder().findAndAddModules().build()

@ApplicationScoped
class ModerationSubscriber {
    companion object {
        private const val MESSAGE_MODERATION_CHANNEL = "message-moderate"
    }

    @Incoming(MESSAGE_MODERATION_CHANNEL)
    @Retry(delay = 5, maxRetries = 2)
    fun consume(event: CloudEventV1) {
        var exceptionThrown = false
        logger.debug { "event received. ${event.toLogString(MESSAGE_MODERATION_CHANNEL)}"}
        try {
            val json = String(event.data?.toBytes() ?: ByteArray(0))
            val payload: QubeMessageEvent = mapper.readValue(json)
            // TODO: implement moderation
        } catch (e: Exception) {
            exceptionThrown = true
            throw EventException("event processing failed: ${event.toLogString(MESSAGE_MODERATION_CHANNEL)}. Error: ${e.message}", e)
        } finally {
            if (!exceptionThrown) {
                logger.info { "event processed successfully: ${event.toLogString(MESSAGE_MODERATION_CHANNEL)}"}
            }
        }
    }

}
