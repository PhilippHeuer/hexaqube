package qube.core.storage.sourcecode.domain

enum class SourceCodeReferenceType {
    CLASS,
    METHOD;

    companion object {
        fun from(value: String): SourceCodeReferenceType {
            return valueOf(value.uppercase())
        }
    }
}
