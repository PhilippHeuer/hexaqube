package qube.extension.codefinder.extension

import com.github.javaparser.ast.nodeTypes.NodeWithJavadoc
import com.github.javaparser.javadoc.JavadocBlockTag
import java.util.Optional

fun NodeWithJavadoc<*>.getJavadocSince(): Optional<String> {
    return javadoc.flatMap { doc ->
        doc.blockTags
            .stream()
            .filter { it.type == JavadocBlockTag.Type.SINCE }
            .map { it.content.toText() }
            .findFirst()
            .map { Optional.of(it) }
            .orElse(Optional.empty())
    }
}

fun NodeWithJavadoc<*>.getJavadocDescription(): Optional<String> {
    return javadoc.flatMap { doc ->
        doc.description.toText().let { Optional.of(it) }
    }
}

fun NodeWithJavadoc<*>.getJavadocTags(): List<Map<String, String>> {
    return javadoc.map { doc ->
        doc.blockTags
            .stream()
            .map { tag ->
                mapOf(
                    "type" to tag.type.name,
                    "name" to tag.name.orElse(null),
                    "content" to tag.content.toText()
                )
            }
            .toList()
    }.orElseGet { emptyList() }
}
