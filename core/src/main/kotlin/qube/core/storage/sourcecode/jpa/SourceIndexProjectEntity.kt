package qube.core.storage.sourcecode.jpa

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import org.hibernate.annotations.GenericGenerator
import qube.core.storage.sourcecode.domain.SourceCodeProjectType
import java.time.Instant
import java.util.UUID

@Entity(name = "sourceindex_project")
data class SourceIndexProjectEntity(

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    var id: UUID? = null,

    @Column(name = "KEY", nullable = false)
    var key: String? = null,

    @Column(name = "TYPE", length = 50)
    @Enumerated(EnumType.STRING)
    var type: SourceCodeProjectType? = null,

    @Column(name = "DISPLAY_NAME", length = 150)
    var displayName: String? = null,

    @Column(name = "DESCRIPTION", length = 500)
    var description: String? = null,

    @Column(name = "REPOSITORY_KIND", nullable = false)
    var repositoryKind: String = "",

    @Column(name = "REPOSITORY_REMOTE", nullable = false)
    var repositoryRemote: String = "",

    @Column(name = "REPOSITORY_COMMIT_HASH", nullable = false)
    var repositoryCommitHash: String? = null,

    @Column(name = "REPOSITORY_TAG", nullable = false)
    var repositoryTag: String? = null,

    @Column(name = "CREATED_AT", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "UPDATED_AT", nullable = false)
    var updatedAt: Instant = Instant.now()

) : PanacheEntityBase {
    companion object : PanacheCompanion<SourceIndexProjectEntity> {

    }
}
