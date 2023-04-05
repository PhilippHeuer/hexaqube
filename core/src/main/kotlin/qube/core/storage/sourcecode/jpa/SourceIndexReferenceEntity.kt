package qube.core.storage.sourcecode.jpa

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import qube.core.storage.sourcecode.domain.SourceCodeReferenceType
import java.util.UUID

@Entity(name = "sourceindex_reference")
data class SourceIndexReferenceEntity(
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID")
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "PROJECT_ID", nullable = false)
    var project: SourceIndexProjectEntity? = null,

    @Column(name = "TYPE", length = 50)
    @Enumerated(EnumType.STRING)
    var type: SourceCodeReferenceType? = null,

    @Column(name = "SELECTOR", length = 600)
    var selector: String? = null,

    @Column(name = "NAMESPACE", length = 500)
    var namespace: String? = null,

    @Column(name = "NAME", length = 300)
    var name: String? = null,

    @Column(name = "SOURCE_FILE", length = 150)
    var sourceFile: String? = null,

    @Column(name = "SOURCE_LINE_FROM", length = 10)
    var sourceLine: Int = 0,

    @Column(name = "SOURCE_LINE_TO", length = 10)
    var sourceLineEnd: Int = 0,

    @Column(name = "ADDED_IN", length = 20)
    var addedIn: String? = null,

    @Column(name = "REMOVED_IN", length = 20)
    var removedIn: String? = null,

    @Column(name = "FLAGS")
    var flags: Int = 0,

    @Column(name = "DEFINITION", length = 1000)
    var definition: String? = null,

    @Column(name = "DATA", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    var data: Map<String, Any>? = null
) : PanacheEntityBase {

    companion object : PanacheCompanion<SourceIndexReferenceEntity> {

        fun findByProject(project: SourceIndexProjectEntity): List<SourceIndexReferenceEntity> {
            return find("project", project).list()
        }
    }
}
