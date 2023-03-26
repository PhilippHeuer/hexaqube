package qube.core.eventbus.domain

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

/**
 * Role
 *
 * @param id unique id for this role
 * @param scope scope of the role (global, instance, ...)
 * @param name name of the role
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class QubeRole(
    val id: String,
    val scope: String,
    val name: String,
)
