package qube.core.eventbus.domain

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

/**
 * Message Reaction
 *
 * @param name name of the reaction
 * @param type type of the reaction (unicode, emote)
 * @param count total count
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class QubeReaction(
    val name: String,
    val type: String,
    val count: Int,
)
