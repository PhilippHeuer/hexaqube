package qube.core.event.domain

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class QubeMessage(
    // this is the id of the message
    val id: String,
    // this is the text of the message
    val text: String = "",
    // this is the list of attachments of the message
    val attachments: List<QubeMessageAttachment> = mutableListOf(),
    // this is the list of reactions on the message
    val reactions: MutableList<QubeReaction> = mutableListOf(),
    // this is the id of the message this message is a response to
    val responseTo: String? = null,
    // this is arbitrary data that can be attached to the message
    val data: Map<String, Any> = mutableMapOf(),

    val command: String? = null,
    val commandPayload: String? = null,
)
