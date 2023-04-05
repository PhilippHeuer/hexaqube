package qube.core.event.events

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import qube.core.event.domain.QubeInstance
import java.net.URI

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class QubeCommandResponseEvent(
    @JsonIgnore
    override val eventSource: URI?,
    @JsonProperty("instance")
    val instance: QubeInstance,
    @JsonProperty("command")
    val command: QubeCommandEvent,
    @JsonProperty("template_id")
    val templateId: String,
    @JsonProperty("template_data")
    val templateData: Map<String, Any>,
): QubeEvent
