package qube.codefinder.util

import com.vdurmont.semver4j.Semver
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref

class RepositoryUtils {

    companion object {
        /**
         * returns a map of all tags of the given repository without cloning it
         *
         * @return a map of all tags, indexed by the git reference, for example refs/tags/1.0.0
         */
        fun getRepositoryReferences(remoteUrl: String): Collection<Ref> {
            return Git.lsRemoteRepository()
                .setHeads(false)
                .setTags(true)
                .setRemote(remoteUrl)
                .call()
        }

        /**
         * @return true if the given reference is a stable version tag, false otherwise
         */
        fun isStableRepositoryReference(ref: String): Boolean {
            return ref.startsWith("refs/tags/v") && Semver(ref.removePrefix("refs/tags/v"), Semver.SemverType.LOOSE).isStable
        }
    }

}
