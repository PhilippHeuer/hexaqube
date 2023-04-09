package qube.codefinder.parser.domain

data class SourceCodeSymbol(
    val selector: String,
    val language: String,
    val type: String,
    val namespace: String,
    val name: String,
    val description: String? = null,
    val file: String,
    val lineBegin: Int,
    val lineEnd: Int,
    val length: Int,
    var addedIn: String? = null,
    var removedIn: String? = null,
    val flags: Set<SourceCodeSymbolFlag> = emptySet(),
    val properties: Map<String, Any> = emptyMap(),
    val definition: String,
)
