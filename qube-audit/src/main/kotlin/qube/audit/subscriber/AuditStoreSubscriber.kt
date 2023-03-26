package qube.audit.subscriber

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.cloudevents.core.v1.CloudEventV1
import io.github.oshai.KotlinLogging
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheEntityBase
import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.faulttolerance.Retry
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.hibernate.reactive.mutiny.Mutiny
import qube.core.eventbus.domain.QubeAuditType
import qube.core.eventbus.domain.QubeMessageActionType
import qube.core.eventbus.events.QubeMessageEvent
import qube.core.exception.EventException
import qube.core.eventbus.extensions.toLogString
import qube.core.jpa.entity.AuditEntity
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.context.control.ActivateRequestContext

private val logger = KotlinLogging.logger {}
private val mapper = jacksonObjectMapper()

@ApplicationScoped
class AuditStoreSubscriber(
    val session: Mutiny.Session
) {
    companion object {
        private const val MESSAGE_AUDIT_CHANNEL = "message-audit"
    }

    @Incoming(MESSAGE_AUDIT_CHANNEL)
    @Retry(delay = 5, maxRetries = 2)
    @ActivateRequestContext
    fun consume(event: CloudEventV1): Uni<Void?>? {
        var exceptionThrown = false
        logger.debug { "event received: ${event.toLogString(MESSAGE_AUDIT_CHANNEL)}"}
        try {
            val json = String(event.data?.toBytes() ?: ByteArray(0))
            val payload: QubeMessageEvent = mapper.readValue(json)

            var auditRecord = AuditEntity()
            if (payload.action == QubeMessageActionType.CREATE) {
                auditRecord.type = QubeAuditType.MESSAGE_CREATE
            } else if (payload.action == QubeMessageActionType.UPDATE) {
                auditRecord.type = QubeAuditType.MESSAGE_UPDATE
            }

            return session.withTransaction {
                auditRecord.persistAndFlush<PanacheEntityBase>().replaceWithVoid()
            }.onTermination().call { _, _, _ -> session.close() }
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
