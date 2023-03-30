package qube.feature.sourcecode.domain

enum class SourceCodeSymbolFlag(val value: Int) {
    DEFAULT(1 shl 0),
    DEPRECATED(1 shl 1), // may be changed incompatibly or removed in a future version
    EXPERIMENTAL(1 shl 2), // may be changed or removed in a future version
    INTERNAL(1 shl 3); // not intended for public use

    companion object {
        fun ofSet(flags: Set<SourceCodeSymbolFlag>): Int {
            return flags.fold(0) { acc, flag -> acc or flag.value }
        }
    }
}
