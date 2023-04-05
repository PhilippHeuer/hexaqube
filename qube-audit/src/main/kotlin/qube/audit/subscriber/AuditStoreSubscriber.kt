package qube.audit.subscriber

import io.cloudevents.core.v1.CloudEventV1
import io.github.oshai.KotlinLogging
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.infrastructure.Infrastructure
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import org.eclipse.microprofile.reactive.messaging.Incoming
import qube.core.event.domain.QubeAuditType
import qube.core.event.domain.QubeMessageActionType
import qube.core.event.events.QubeMessageEvent
import qube.core.event.extensions.toLogString
import qube.core.storage.audit.jpa.AuditEntity
import qube.core.util.QubeObjectMapper

@ApplicationScoped
class AuditStoreSubscriber {
    @Incoming("message-audit")
    @Transactional
    fun consume(cloudEvent: CloudEventV1): Uni<Void> {
        logger.debug { "event received: ${cloudEvent.toLogString()}"}

        return Uni.createFrom().item(cloudEvent)
            .map { QubeObjectMapper.json.readValue(it.data?.toBytes() ?: ByteArray(0), QubeMessageEvent::class.java) }
            .emitOn(Infrastructure.getDefaultWorkerPool())
            .onItem().invoke { event ->
                val auditRecord = AuditEntity()
                if (event.action == QubeMessageActionType.CREATE) {
                    auditRecord.type = QubeAuditType.MESSAGE_CREATE
                } else if (event.action == QubeMessageActionType.UPDATE) {
                    auditRecord.type = QubeAuditType.MESSAGE_UPDATE
                }
                auditRecord.persistAndFlush()
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
