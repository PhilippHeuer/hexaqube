package qube.core.event.events

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import qube.core.event.domain.QubeChannel
import qube.core.event.domain.QubeInstance
import qube.core.event.domain.QubeMessage
import qube.core.event.domain.QubeMessageActionType
import qube.core.event.domain.QubeUser
import java.net.URI

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class QubeMessageEvent(
    @JsonIgnore
    override val eventSource: URI?,
    @JsonProperty("instance")
    val instance: QubeInstance,
    @JsonProperty("channel")
    val channel: QubeChannel,
    @JsonProperty("user")
    val user: QubeUser,
    @JsonProperty("message")
    val message: QubeMessage,
    @JsonProperty("action")
    val action: QubeMessageActionType,
): QubeEvent
