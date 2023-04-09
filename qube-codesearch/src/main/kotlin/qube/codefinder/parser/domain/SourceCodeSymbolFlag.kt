package qube.codefinder.parser.domain

enum class SourceCodeSymbolFlag {
    DEPRECATED, // may be changed incompatibly or removed in a future version
    EXPERIMENTAL, // may be changed or removed in a future version
    INTERNAL, // not intended for public use
    UNOFFICIAL; // backend by unofficial APIs
}
