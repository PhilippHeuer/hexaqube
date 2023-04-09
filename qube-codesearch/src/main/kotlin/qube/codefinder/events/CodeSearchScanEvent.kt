package qube.codefinder.events

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import qube.core.event.events.QubeEvent
import qube.core.storage.codesearch.jpa.SourceIndexProjectEntity
import java.net.URI

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class CodeSearchScanEvent(
    @JsonIgnore
    override val eventSource: URI? = URI.create("/jobs/qube-sourcecode/indexproject"),
    @JsonProperty("project")
    val project: SourceIndexProjectEntity,
): QubeEvent
