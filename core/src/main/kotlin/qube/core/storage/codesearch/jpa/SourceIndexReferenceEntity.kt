package qube.core.storage.codesearch.jpa

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
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
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField
import org.hibernate.type.SqlTypes
import qube.core.storage.codesearch.domain.SourceCodeReferenceType
import java.util.UUID

@Entity(name = "sourceindex_reference")
@Indexed
data class SourceIndexReferenceEntity(
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID")
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "PROJECT_ID", nullable = false)
    var project: SourceIndexProjectEntity? = null,

    @Column(name = "LANGUAGE", length = 50)
    var language: String? = null,

    @Column(name = "TYPE", length = 50)
    @Enumerated(EnumType.STRING)
    var type: SourceCodeReferenceType? = null,

    @Column(name = "SELECTOR", length = 600)
    @FullTextField(analyzer = "pattern_selector")
    var selector: String? = null,

    @Column(name = "NAMESPACE", length = 500)
    var namespace: String? = null,

    @Column(name = "NAME", length = 300)
    @FullTextField(analyzer = "pattern_selector")
    var name: String? = null,

    @Column(name = "DESCRIPTION", length = 3000)
    @FullTextField(analyzer = "text")
    var description: String? = null,

    @Column(name = "SOURCE_FILE", length = 150)
    var sourceFile: String? = null,

    @Column(name = "SOURCE_LINE_FROM", length = 10)
    var sourceLine: Int = 0,

    @Column(name = "SOURCE_LINE_TO", length = 10)
    var sourceLineEnd: Int = 0,

    @Column(name = "SOURCE_LINK", length = 500)
    var sourceLink: String? = null,

    @Column(name = "ADDED_IN", length = 20)
    var firstSeenIn: String? = null,

    @Column(name = "REMOVED_IN", length = 20)
    var lastSeenIn: String? = null,

    @Column(name = "FLAGS")
    @ElementCollection
    @FullTextField(analyzer = "text")
    var flags: List<String> = emptyList(),

    @Column(name = "DEFINITION", length = 1000)
    var definition: String? = null,

    @Column(name = "DATA", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    var data: Map<String, Any>? = null,
) : PanacheEntityBase {
    companion object : PanacheCompanion<SourceIndexReferenceEntity> {
        fun findByProject(project: SourceIndexProjectEntity): List<SourceIndexReferenceEntity> {
            return find("project", project).list()
        }
    }
}
