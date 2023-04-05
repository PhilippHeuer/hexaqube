package qube.feature.sourcecode.controller

import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.fasterxml.jackson.module.kotlin.readValue
import io.cloudevents.core.v1.CloudEventV1
import io.github.oshai.KotlinLogging
import io.smallrye.common.annotation.Blocking
import io.smallrye.mutiny.Multi
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import org.eclipse.jgit.api.Git
import org.eclipse.microprofile.faulttolerance.Retry
import org.eclipse.microprofile.reactive.messaging.Incoming
import qube.core.eventbus.extensions.toLogString
import qube.core.exception.EventException
import qube.core.storage.sourcecode.domain.SourceCodeReferenceType
import qube.core.storage.sourcecode.jpa.SourceIndexProjectEntity
import qube.core.storage.sourcecode.jpa.SourceIndexReferenceEntity
import qube.feature.sourcecode.domain.SourceCodeSymbol
import qube.feature.sourcecode.domain.SourceCodeSymbolFlag
import qube.feature.sourcecode.events.SourceCodeScanEvent
import qube.feature.sourcecode.parser.JavaProjectIndexer
import qube.feature.sourcecode.parser.ProjectIndexer
import java.io.File
import java.nio.file.Files
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.PathWalkOption
import kotlin.io.path.absolutePathString
import kotlin.io.path.walk

private val logger = KotlinLogging.logger {}
private val mapper = jacksonMapperBuilder().findAndAddModules().build()

@ApplicationScoped
class SourceCodeJobSubscriber {
    companion object {
        private const val JOB_INDEXPROJECT_CHANNEL = "in-job-indexproject"
    }

    @Incoming(JOB_INDEXPROJECT_CHANNEL)
    @Retry(delay = 5, maxRetries = 0)
    @Transactional
    @Blocking
    fun consume(event: CloudEventV1) {
        logger.debug { "event received: ${event.toLogString(JOB_INDEXPROJECT_CHANNEL)}"}
        try {
            val json = String(event.data?.toBytes() ?: ByteArray(0))
            val payload: SourceCodeScanEvent = mapper.readValue(json)

            // process event
            val dbReferences = SourceIndexReferenceEntity.findByProject(project = payload.project).associateBy { it.selector }.toMutableMap()
            processProject(payload.project)
                .map { symbol ->
                    val ref = dbReferences.remove(symbol.selector) ?: SourceIndexReferenceEntity(
                        project = payload.project,
                        type = SourceCodeReferenceType.from(symbol.type),
                        selector = symbol.selector,
                        namespace = symbol.namespace,
                        name = symbol.name
                    )
                    ref.sourceFile = symbol.file
                    ref.sourceLine = symbol.lineBegin
                    ref.sourceLineEnd = symbol.lineEnd
                    if (symbol.addedIn != null) {
                        ref.addedIn = symbol.addedIn
                    }
                    ref.flags = symbol.flags
                    ref.data = symbol.properties

                    // automatically set addedIn to the first version we have seen the symbol in
                    if (ref.addedIn == null) {
                        ref.addedIn = payload.project.repositoryTag
                    }

                    ref
                }
                // collect all references and persist them
                .collect().asList().subscribe().with { refs ->
                    // mark all references that are not in the current index as removed in the current version
                    dbReferences.values.forEach { ref ->
                        ref.removedIn = payload.project.repositoryTag
                        refs.add(ref)
                    }

                    // save all references
                    SourceIndexReferenceEntity.persist(refs)
                    logger.info {
                        val deprecated = refs.count { (it.flags and SourceCodeSymbolFlag.DEPRECATED.value) == SourceCodeSymbolFlag.DEPRECATED.value }
                        val internal = refs.count { (it.flags and SourceCodeSymbolFlag.INTERNAL.value) == SourceCodeSymbolFlag.INTERNAL.value }
                        val experimental = refs.count { (it.flags and SourceCodeSymbolFlag.EXPERIMENTAL.value) == SourceCodeSymbolFlag.EXPERIMENTAL.value }
                        "persisted ${refs.size} symbols [EXPERIMENTAL: $experimental, DEPRECATED: $deprecated, INTERNAL: $internal] into the database for project: ${payload.project.id}"
                    }
                }
        } catch (e: Exception) {
            throw EventException("event processing failed: ${event.toLogString(JOB_INDEXPROJECT_CHANNEL)}. Error: ${e.message}", e)
        } finally {
            logger.info { "event processed successfully: ${event.toLogString(JOB_INDEXPROJECT_CHANNEL)}"}
        }
    }

    @OptIn(ExperimentalPathApi::class)
    @Blocking
    fun processProject(project: SourceIndexProjectEntity): Multi<SourceCodeSymbol> {
        return Multi.createFrom().emitter { emitter ->
            val tempDir = Files.createTempDirectory("hexaqube-indexproject-")
            val remoteRef = "refs/tags/v${project.repositoryTag}"
            val indexer: List<ProjectIndexer> = listOf(JavaProjectIndexer(tempDir))
            logger.debug { "cloning repository: ${project.repositoryRemote} / $remoteRef to ${tempDir.toFile().absolutePath}" }

            // clone
            val git = Git.cloneRepository()
                .setURI(project.repositoryRemote)
                .setBranch(remoteRef)
                .setDirectory(tempDir.toFile())
                .call()
            git.close()
            logger.info { "cloned repository: ${project.repositoryRemote} / $remoteRef to ${tempDir.toFile().absolutePath}" }

            // get file list
            val files = tempDir.walk(PathWalkOption.INCLUDE_DIRECTORIES)
                .filter { path -> !path.absolutePathString().contains(File.separator + ".git" + File.separator) }
                .map { path -> path.toFile() }
                .distinct()
                .toList()
            logger.info { "found ${files.size} files in repository ${tempDir.toFile().absolutePath}" }

            try {
                // index files
                files.forEach { file ->
                    indexer.forEach { indexer ->
                        if (indexer.supportsFile(file)) {
                            indexer.indexFile(file).forEach {
                                emitter.emit(it)
                            }
                        }
                    }
                }
            } finally {
                tempDir.toFile().deleteRecursively()
            }

            emitter.complete()
        }
    }
}
