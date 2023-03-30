package qube.feature.sourcecode.events

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import qube.core.eventbus.events.QubeEvent
import qube.core.storage.sourcecode.jpa.SourceIndexProjectEntity
import java.net.URI

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class SourceCodeScanEvent(
    @JsonProperty("event_source")
    override val eventSource: URI = URI.create("/jobs/qube-sourcecode/indexproject"),
    @JsonProperty("event_type")
    override val eventType: String = "qube.jobs.indexproject",
    @JsonProperty("project")
    val project: SourceIndexProjectEntity,
): QubeEvent
