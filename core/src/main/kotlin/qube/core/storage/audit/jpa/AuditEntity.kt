package qube.core.storage.audit.jpa

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.Entity
import qube.core.event.domain.QubeAuditType

@Entity(name = "audit_log")
class AuditEntity : PanacheEntity() {
    companion object: PanacheCompanion<AuditEntity> {
        //fun findByName(name: String) = find("name", name).firstResult()
    }

    lateinit var type: QubeAuditType

}
