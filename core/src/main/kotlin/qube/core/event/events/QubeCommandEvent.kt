package qube.core.event.events

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import qube.core.event.domain.QubeInstance
import java.net.URI
import java.time.Instant

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class QubeCommandEvent(
    @JsonIgnore
    override val eventSource: URI?,
    @JsonProperty("instance")
    val instance: QubeInstance,
    @JsonProperty("command_id")
    val commandId: String,
    @JsonProperty("command_name")
    val commandName: String,
    @JsonProperty("parameters")
    val parameters: Map<String, String>,
    @JsonProperty("created_at")
    val createdAt: Instant,
): QubeEvent
