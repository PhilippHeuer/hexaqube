package qube.core.eventbus.events

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import qube.core.eventbus.domain.QubeChannel
import qube.core.eventbus.domain.QubeInstance
import qube.core.eventbus.domain.QubeMessage
import qube.core.eventbus.domain.QubeMessageActionType
import qube.core.eventbus.domain.QubeUser
import java.net.URI

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class QubeMessageEvent(
    @JsonProperty("event_source")
    override val eventSource: URI,
    @JsonProperty("event_type")
    override val eventType: String,
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
