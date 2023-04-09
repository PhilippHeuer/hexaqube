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
import org.hibernate.search.engine.search.query.SearchResult
import org.hibernate.search.mapper.orm.session.SearchSession
import qube.codefinder.parser.domain.SourceCodeSymbolFlag
import qube.core.event.events.QubeCommandEvent
import qube.core.event.events.QubeCommandResponseEvent
import qube.core.event.extensions.toLogString
import qube.core.storage.codesearch.jpa.SourceIndexReferenceEntity
import qube.core.util.QubeObjectMapper

@ApplicationScoped
class CodeSearchCommand(
    @Channel("publish-command-response")
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 500)
    private var emitter: Emitter<CloudEvent>,
    private val searchSession: SearchSession,
) {
    private val flagNotes = mapOf(
        SourceCodeSymbolFlag.DEPRECATED.name to "This method is deprecated and will be removed in a future release.",
        SourceCodeSymbolFlag.INTERNAL.name to "This method is internal and not intended for use outside of the library.",
        SourceCodeSymbolFlag.EXPERIMENTAL.name to "This method is experimental and subject to change in future releases.",
        SourceCodeSymbolFlag.UNOFFICIAL.name to "This method is not part of the official API and using it may be against the terms of service. Use at your own risk."
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

                // search
                val result = searchSession.search(SourceIndexReferenceEntity::class.java).where { q ->
                    q.bool { b ->
                        // matching
                        b.should { f ->
                            f.match()
                                .fields("name")
                                .matching(selector)
                                .boost(3f)
                        }
                        b.should { f ->
                            f.match()
                                .fields("selector")
                                .matching(selector)
                                .boost(2f)
                                .fuzzy(1)
                        }

                        // ranking
                        b.should { f ->
                            f.match()
                                .fields("flags")
                                .matching("DEPRECATED")
                                .boost(0.5f)
                        }
                        b.should { f ->
                            f.match()
                                .fields("flags")
                                .matching("INTERNAL")
                                .boost(0.4f)
                        }
                        b.should { f ->
                            f.match()
                                .fields("flags")
                                .matching("EXPERIMENTAL")
                                .boost(0.7f)
                        }
                        b.should { f ->
                            f.match()
                                .fields("flags")
                                .matching("UNOFFICIAL")
                                .boost(0.9f)
                        }
                    }
                }.fetch(6) as SearchResult<SourceIndexReferenceEntity>

                // send response
                val match = result.hits().firstOrNull()
                if (match != null) {
                    val notes = match.flags.mapNotNull { flag -> flagNotes[flag] }.toMutableList()

                    val response = QubeCommandResponseEvent(
                        eventSource = cloudEvent.source,
                        instance = event.instance,
                        command = event,
                        templateId = "command.response.codesearch.ok",
                        templateData = mapOf(
                            "result" to match,
                            "result_count" to result.total().hitCount(),
                            "similar" to if (result.hits().size > 1) result.hits().subList(1, result.hits().size) else emptyList(),
                            "notes" to notes,
                        )
                    )
                    emitter.send(response.toEventMessage(topic = "qube.discord.command.response"))
                } else {
                    val response = QubeCommandResponseEvent(
                        eventSource = cloudEvent.source,
                        instance = event.instance,
                        command = event,
                        templateId = "command.response.codesearch.err",
                        templateData = mapOf(
                            "query" to selector,
                        )
                    )
                    emitter.send(response.toEventMessage(topic = "qube.discord.command.response"))
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
