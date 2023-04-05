package qube.audit.subscriber

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.cloudevents.core.v1.CloudEventV1
import io.github.oshai.KotlinLogging
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.context.control.ActivateRequestContext
import org.eclipse.microprofile.faulttolerance.Retry
import org.eclipse.microprofile.reactive.messaging.Incoming
import qube.core.eventbus.domain.QubeAuditType
import qube.core.eventbus.domain.QubeMessageActionType
import qube.core.eventbus.events.QubeMessageEvent
import qube.core.eventbus.extensions.toLogString
import qube.core.exception.EventException
import qube.core.storage.audit.jpa.AuditEntity

private val logger = KotlinLogging.logger {}
private val mapper = jacksonObjectMapper()

@ApplicationScoped
class AuditStoreSubscriber {
    companion object {
        private const val MESSAGE_AUDIT_CHANNEL = "message-audit"
    }

    @Incoming(MESSAGE_AUDIT_CHANNEL)
    @Retry(delay = 5, maxRetries = 2)
    @ActivateRequestContext
    fun consume(event: CloudEventV1) {
        var exceptionThrown = false
        logger.debug { "event received: ${event.toLogString(MESSAGE_AUDIT_CHANNEL)}"}
        try {
            val json = String(event.data?.toBytes() ?: ByteArray(0))
            val payload: QubeMessageEvent = mapper.readValue(json)

            val auditRecord = AuditEntity()
            if (payload.action == QubeMessageActionType.CREATE) {
                auditRecord.type = QubeAuditType.MESSAGE_CREATE
            } else if (payload.action == QubeMessageActionType.UPDATE) {
                auditRecord.type = QubeAuditType.MESSAGE_UPDATE
            }

            auditRecord.persistAndFlush()
        } catch (e: Exception) {
            exceptionThrown = true
            throw EventException("event processing failed: ${event.toLogString(MESSAGE_AUDIT_CHANNEL)}. Error: ${e.message}", e)
        } finally {
            if (!exceptionThrown) {
                logger.info { "event processed successfully: ${event.toLogString(MESSAGE_AUDIT_CHANNEL)}"}
            }
        }
    }
}
