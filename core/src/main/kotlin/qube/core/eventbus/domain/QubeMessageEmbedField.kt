package qube.core.eventbus.domain

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

/**
 * Message Embed Field
 *
 * @param key title
 * @param value content
 * @param inline Inline?
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class QubeMessageEmbedField(
    val key: String,
    val value: String,
    val inline: Boolean,
)
