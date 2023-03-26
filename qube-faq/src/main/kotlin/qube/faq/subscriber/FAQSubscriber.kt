package qube.faq.subscriber

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.cloudevents.core.v1.CloudEventV1
import io.github.oshai.KotlinLogging
import org.eclipse.microprofile.faulttolerance.Retry
import org.eclipse.microprofile.reactive.messaging.Incoming
import qube.core.eventbus.events.QubeMessageEvent
import qube.core.exception.EventException
import qube.core.eventbus.extensions.toLogString
import javax.enterprise.context.ApplicationScoped

private val logger = KotlinLogging.logger {}
private val mapper = jacksonObjectMapper()

@ApplicationScoped
class FAQSubscriber {
    companion object {
        private const val MESSAGE_FAQ_CHANNEL = "message-faq"
    }

    @Incoming(MESSAGE_FAQ_CHANNEL)
    @Retry(delay = 5, maxRetries = 2)
    fun consume(event: CloudEventV1) {
        var exceptionThrown = false
        logger.debug { "event received: ${event.toLogString(MESSAGE_FAQ_CHANNEL)}"}
        try {
            val json = String(event.data?.toBytes() ?: ByteArray(0))
            val payload: QubeMessageEvent = mapper.readValue(json)
            // TODO: implement the logic to check the faq and respond with the answer
        } catch (e: Exception) {
            exceptionThrown = true
            throw EventException("event processing failed: ${event.toLogString(MESSAGE_FAQ_CHANNEL)}. Error: ${e.message}", e)
        } finally {
            if (!exceptionThrown) {
                logger.info { "event processed successfully: ${event.toLogString(MESSAGE_FAQ_CHANNEL)}"}
            }
        }
    }

}
