package qube.codefinder.service

import io.github.oshai.KotlinLogging
import io.smallrye.common.annotation.Blocking
import io.smallrye.mutiny.Multi
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.jgit.api.Git
import qube.core.storage.codesearch.jpa.SourceIndexProjectEntity
import qube.codefinder.parser.JavaProjectIndexer
import qube.codefinder.parser.ProjectIndexer
import qube.codefinder.parser.domain.SourceCodeSymbol
import java.io.File
import java.nio.file.Files
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.PathWalkOption
import kotlin.io.path.absolutePathString
import kotlin.io.path.walk

/**
 * this code is responsible for indexing the source code and storing it in the database
 */
@ApplicationScoped
class SourceCodeIndexService {

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

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
