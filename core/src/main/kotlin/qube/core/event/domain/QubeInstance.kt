package qube.core.event.domain

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class QubeInstance(
    // this is the platform type
    val type: String,
    // this is the instance id on the platform
    val id: String
) {
    /**
     * this is a globally unique key for each instance
     */
    fun getKey(): String {
        return "$type-$id"
    }
}
