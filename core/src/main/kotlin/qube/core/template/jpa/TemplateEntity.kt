package qube.core.template.jpa

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import org.hibernate.annotations.GenericGenerator
import qube.core.event.domain.QubeInstance
import java.util.UUID

@Entity(name = "template")
data class TemplateEntity(
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID")
    var id: UUID? = null,

    @Column(name = "TEMPLATE_ID", length = 100)
    var templateId: String? = null,

    @Column(name = "INSTANCE_TYPE", length = 50)
    var instanceType: String? = null,

    @Column(name = "INSTANCE_ID", length = 150)
    var instanceId: String? = null,

    @Column(name = "TITLE", length = 100)
    var title: String? = null,

    @Column(name = "CONTENT", length = 1000)
    var content: String? = null,

    @Column(name = "FOOTER", length = 100)
    var footer: String? = null,

) : PanacheEntityBase {

    companion object : PanacheCompanion<TemplateEntity> {
        fun findByTemplateId(templateId: String): List<TemplateEntity> {
            return find("templateId = ?1 and instanceType is null and instanceId is null", templateId).list()
        }

        fun findByTemplateIdAndInstance(templateId: String, instance: QubeInstance): List<TemplateEntity> {
            return find("templateId = ?1 and instanceType = ?2 and instanceId = ?3", templateId, instance.type, instance.id).list()
        }
    }
}
