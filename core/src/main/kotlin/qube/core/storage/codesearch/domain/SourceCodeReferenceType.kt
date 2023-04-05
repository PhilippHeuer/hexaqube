package qube.core.storage.codesearch.domain

enum class SourceCodeReferenceType {
    CLASS,
    METHOD;

    companion object {
        fun from(value: String): SourceCodeReferenceType {
            return valueOf(value.uppercase())
        }
    }
}
