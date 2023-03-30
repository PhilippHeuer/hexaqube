package qube.feature.sourcecode.controller

import com.vdurmont.semver4j.Semver
import io.cloudevents.CloudEvent
import io.github.oshai.KotlinLogging
import io.quarkus.scheduler.Scheduled
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.eclipse.microprofile.reactive.messaging.OnOverflow
import qube.core.service.DistributedLockService
import qube.core.storage.sourcecode.jpa.SourceIndexProjectEntity
import qube.feature.sourcecode.events.SourceCodeScanEvent
import qube.feature.sourcecode.util.RepositoryUtils
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.context.control.ActivateRequestContext

private val logger = KotlinLogging.logger {}

@ApplicationScoped
class SourceCodeJobController(
    private val lockService: DistributedLockService,

    @Channel("out-job-indexproject")
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 500)
    private var jobEmitter: Emitter<CloudEvent>,
) {

    @Scheduled(delay = 5, delayUnit = TimeUnit.SECONDS, every = "1m")
    @ActivateRequestContext
    fun scheduleProjectUpdate() {
        val lock = lockService.getLock("job-indexproject-dispatch", Duration.ofSeconds(30), Duration.ofMinutes(5)).orElse(null) ?: return

        try {
            val projects = SourceIndexProjectEntity.listAll()
            projects.forEach { project ->
                if (project.repositoryKind == "git") {
                    processGITProject(project)
                } else {
                    logger.warn { "unsupported repository kind: ${project.repositoryKind} for project ${project.id}" }
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "error while scheduling project index update" }
        } finally {
            lock.unlock()
        }
    }

    fun processGITProject(project: SourceIndexProjectEntity) {
        val refs = RepositoryUtils.getRepositoryReferences(project.repositoryRemote).stream()
            .filter { RepositoryUtils.isStableRepositoryReference(it.name) }
            .sorted { o1, o2 ->
                val version1 = o1.name.removePrefix("refs/tags/v")
                val version2 = o2.name.removePrefix("refs/tags/v")

                return@sorted Semver(version1, Semver.SemverType.LOOSE).compareTo(Semver(version2, Semver.SemverType.LOOSE))
            }.toList()

        refs.forEach { ref ->
            val version = ref.name.removePrefix("refs/tags/v")

            // submit job if no tag is set or if the tag is older than the last
            if (project.repositoryTag == null || Semver(version, Semver.SemverType.LOOSE) > Semver(project.repositoryTag, Semver.SemverType.LOOSE)) {
                project.repositoryTag = version
                project.repositoryCommitHash = ref.objectId.name

                val event = SourceCodeScanEvent(project = project).toCloudEvent()
                jobEmitter.send(event)
            }
        }

        SourceIndexProjectEntity.persist(project)
    }
}
