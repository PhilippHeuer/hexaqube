package qube.core.event.domain

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import java.awt.Color
import java.time.temporal.TemporalAccessor

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class QubeMessageEmbed(
    val title: String,
    val description: String,
    val url: String,
    val color: Color,
    val footer: String,
    val fields: List<QubeMessageEmbedField>,
    val timestamp: TemporalAccessor,
)
