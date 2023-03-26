package qube.core.eventbus.domain

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import okhttp3.OkHttpClient
import okhttp3.Request

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class QubeMessageAttachment(
    val type: QubeAttachmentType,
    val name: String,
    var bytes: ByteArray?,
    val url: String,
) {
    companion object {
        private val httpClient = OkHttpClient()
    }

    fun getFile(): ByteArray? {
        // download if we got an url and the bytes are not present yet
        if (url.isNotEmpty() && bytes == null) {
            // download attachment
            try {
                val request: Request = Request.Builder().url(url).build()
                val response = httpClient.newCall(request).execute()
                val inputStream = response.body()!!.byteStream()
                bytes = inputStream.readAllBytes()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        return bytes
    }
}
