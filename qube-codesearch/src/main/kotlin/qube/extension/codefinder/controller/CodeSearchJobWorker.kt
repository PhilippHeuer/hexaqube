package qube.extension.codefinder.controller

import com.fasterxml.jackson.module.kotlin.readValue
import io.cloudevents.core.v1.CloudEventV1
import io.github.oshai.KotlinLogging
import io.smallrye.common.annotation.Blocking
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import org.eclipse.microprofile.reactive.messaging.Incoming
import qube.core.event.extensions.toLogString
import qube.core.exception.EventException
import qube.core.storage.codesearch.domain.SourceCodeReferenceType
import qube.core.storage.codesearch.jpa.SourceIndexReferenceEntity
import qube.core.util.QubeObjectMapper
import qube.extension.codefinder.events.CodeSearchScanEvent
import qube.extension.codefinder.parser.domain.SourceCodeSymbolFlag
import qube.extension.codefinder.service.SourceCodeIndexService

@ApplicationScoped
class CodeSearchJobWorker(
    private val sourceCodeIndexService: SourceCodeIndexService
) {
    @Incoming("job-indexproject")
    @Transactional
    @Blocking
    fun consume(event: CloudEventV1) {
        logger.debug { "event received: ${event.toLogString()}"}
        try {
            val json = String(event.data?.toBytes() ?: ByteArray(0))
            val payload: CodeSearchScanEvent = QubeObjectMapper.json.readValue(json)

            // process event
            val dbReferences = SourceIndexReferenceEntity.findByProject(project = payload.project).associateBy { it.selector }.toMutableMap()
            sourceCodeIndexService.processProject(payload.project)
                .map { symbol ->
                    val ref = dbReferences.remove(symbol.selector) ?: SourceIndexReferenceEntity(
                        project = payload.project,
                        language = symbol.language,
                        selector = symbol.selector,
                        namespace = symbol.namespace,
                        name = symbol.name
                    )
                    ref.type = SourceCodeReferenceType.from(symbol.type)
                    ref.description = symbol.description
                    ref.sourceFile = symbol.file
                    ref.sourceLine = symbol.lineBegin
                    ref.sourceLineEnd = symbol.lineEnd
                    ref.sourceLink = payload.project.repositoryRemote.removeSuffix(".git")+"/blob/${payload.project.repositoryCommitHash}/${symbol.file}#L${symbol.lineBegin}-L${symbol.lineEnd}"
                    if (symbol.addedIn != null) {
                        ref.firstSeenIn = symbol.addedIn
                    }
                    ref.flags = symbol.flags
                    ref.definition = symbol.definition
                    ref.data = symbol.properties

                    // set addedIn to the first version we have seen the symbol in
                    if (ref.firstSeenIn == null) {
                        ref.firstSeenIn = payload.project.repositoryTag
                    }

                    ref
                }
                // collect all references and persist them
                .collect().asList().subscribe().with { refs ->
                    // mark all references that are not in the current index as removed in the current version
                    /*
                    dbReferences.values.forEach { ref ->
                        ref.removedIn = payload.project.repositoryTag
                        refs.add(ref)
                    }
                    */

                    // save all references
                    SourceIndexReferenceEntity.persist(refs)
                    logger.debug {
                        val deprecated = refs.count { (it.flags and SourceCodeSymbolFlag.DEPRECATED.value) == SourceCodeSymbolFlag.DEPRECATED.value }
                        val internal = refs.count { (it.flags and SourceCodeSymbolFlag.INTERNAL.value) == SourceCodeSymbolFlag.INTERNAL.value }
                        val experimental = refs.count { (it.flags and SourceCodeSymbolFlag.EXPERIMENTAL.value) == SourceCodeSymbolFlag.EXPERIMENTAL.value }
                        val unofficial = refs.count { (it.flags and SourceCodeSymbolFlag.UNOFFICIAL.value) == SourceCodeSymbolFlag.UNOFFICIAL.value }
                        "persisted ${refs.size} symbols [EXPERIMENTAL: $experimental, DEPRECATED: $deprecated, INTERNAL: $internal, UNOFFICIAL: $unofficial] into the database for project: ${payload.project.id}"
                    }
                }
        } catch (e: Exception) {
            throw EventException("event processing failed: ${event.toLogString()}. Error: ${e.message}", e)
        } finally {
            logger.info { "event processed successfully: ${event.toLogString()}"}
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
