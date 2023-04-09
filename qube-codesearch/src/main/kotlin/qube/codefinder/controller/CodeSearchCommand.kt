package qube.codefinder.controller

import io.cloudevents.CloudEvent
import io.cloudevents.core.v1.CloudEventV1
import io.github.oshai.KotlinLogging
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.infrastructure.Infrastructure
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.eclipse.microprofile.reactive.messaging.OnOverflow
import qube.core.event.events.QubeCommandEvent
import qube.core.event.events.QubeCommandResponseEvent
import qube.core.event.extensions.toLogString
import qube.core.storage.codesearch.jpa.SourceIndexReferenceEntity
import qube.core.util.QubeObjectMapper
import qube.codefinder.parser.domain.SourceCodeSymbolFlag

@ApplicationScoped
class CodeSearchCommand(
    @Channel("publish-command-response")
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 500)
    private var emitter: Emitter<CloudEvent>,
) {
    private val flagNotes = mapOf(
        SourceCodeSymbolFlag.DEPRECATED to "This method is deprecated and will be removed in a future release.",
        SourceCodeSymbolFlag.INTERNAL to "This method is internal and not intended for use outside of the library.",
        SourceCodeSymbolFlag.EXPERIMENTAL to "This method is experimental and subject to change in future releases.",
        SourceCodeSymbolFlag.UNOFFICIAL to "This method is not part of the official API and using it may be against the terms of service. Use at your own risk."
    )

    @Incoming("command-execute-codesearch")
    @Transactional
    fun consume(cloudEvent: CloudEventV1): Uni<Void> {
        logger.debug { "event received: ${cloudEvent.toLogString()}"}

        return Uni.createFrom().item(cloudEvent)
            .map { QubeObjectMapper.json.readValue(it.data?.toBytes() ?: ByteArray(0), QubeCommandEvent::class.java) }
            .emitOn(Infrastructure.getDefaultWorkerPool())
            .onItem().invoke { event ->
                val selector: String = event.parameters["query"]!!

                // find matches
                val matches = SourceIndexReferenceEntity.findBySelector(selector)

                // score matches and pick the best one
                matches.map {
                    it.score = 0.0
                }

                // send response
                val match = matches.firstOrNull()
                if (match != null) {
                    val flags = SourceCodeSymbolFlag.toSet(matches[0].flags)
                    val notes = flags.mapNotNull { flag -> flagNotes[flag] }.toMutableList()

                    val response = QubeCommandResponseEvent(
                        eventSource = cloudEvent.source,
                        instance = event.instance,
                        command = event,
                        templateId = "command.response.codesearch.ok",
                        templateData = mapOf(
                            "result" to match,
                            "result_count" to matches.size,
                            "notes" to notes,
                        )
                    )
                    emitter.send(response.toEventMessage(topic = "qube.discord.command.response"))
                } else {
                    val response = QubeCommandResponseEvent(
                        eventSource = cloudEvent.source,
                        instance = event.instance,
                        command = event,
                        templateId = "command.response.codesearch.ok",
                        templateData = mapOf(
                            "query" to selector,
                        )
                    )
                    emitter.send(response.toEventMessage(topic = "qube.discord.command.err"))
                }
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
