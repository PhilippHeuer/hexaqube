package qube.core.jpa.entity

import io.quarkus.hibernate.reactive.panache.kotlin.PanacheEntity;
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheCompanion
import qube.core.eventbus.domain.QubeAuditType
import javax.persistence.Entity

@Entity(name = "audit_log")
class AuditEntity : PanacheEntity() {
    companion object: PanacheCompanion<AuditEntity> {
        fun findByName(name: String) = find("name", name).firstResult()
    }

    lateinit var type: QubeAuditType

}
