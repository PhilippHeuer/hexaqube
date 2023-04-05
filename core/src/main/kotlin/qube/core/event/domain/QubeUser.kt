package qube.core.event.domain

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class QubeUser(
    val id: String,
    val name: String? = null,
    val displayName: String? = null,
    val avatarUrl: String? = null,
    val mention: String? = null,
    val status: String? = null,
    val bot: Boolean = false,
    val fake: Boolean = false, // fictional accounts
    val roles: Set<QubeRole> = mutableSetOf(),
    @JsonIgnore
    val origin: Any? = null,
)
