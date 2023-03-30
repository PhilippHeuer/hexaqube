package qube.core.storage.sourcecode.jpa

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import qube.core.storage.sourcecode.domain.SourceCodeReferenceType
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "SOURCEINDEX_REFERENCE")
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
    var data: Map<String, Any>? = null
) : PanacheEntityBase {

    companion object : PanacheCompanion<SourceIndexReferenceEntity> {

        fun findByProject(project: SourceIndexProjectEntity): List<SourceIndexReferenceEntity> {
            return find("project", project).list()
        }
    }
}
