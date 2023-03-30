package qube.core.storage.sourcecode.domain

enum class SourceCodeCommentType {
    DESCRIPTION,
    PARAMETER,
    DEPRECATED,
    SINCE,
    AUTHOR,
    LINK,
    EXCEPTION,
    RETURN,
    UNKNOWN;

    companion object {
        fun of(type: String): SourceCodeCommentType {
            return when (type) {
                "PARAM" -> PARAMETER
                "RETURN" -> RETURN
                "DEPRECATED" -> DEPRECATED
                "SINCE" -> SINCE
                "AUTHOR" -> AUTHOR
                "SEE" -> LINK
                "THROWS" -> EXCEPTION
                "EXCEPTION" -> EXCEPTION
                else -> UNKNOWN
            }
        }
    }
}
