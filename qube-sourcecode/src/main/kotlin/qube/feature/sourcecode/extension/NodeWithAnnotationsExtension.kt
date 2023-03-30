package qube.feature.sourcecode.extension

import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations
import org.jetbrains.annotations.ApiStatus
import qube.feature.sourcecode.domain.SourceCodeSymbolFlag
import java.lang.Deprecated
import java.util.Optional

fun NodeWithAnnotations<*>.getFlags(): Set<SourceCodeSymbolFlag> {
    return mutableSetOf(SourceCodeSymbolFlag.DEFAULT).apply {
        if (isAnnotationPresent(Deprecated::class.java) || isAnnotationPresent(kotlin.Deprecated::class.java) || isAnnotationPresent(ApiStatus.ScheduledForRemoval::class.java) || isAnnotationPresent(ApiStatus.Obsolete::class.java)) {
            add(SourceCodeSymbolFlag.DEPRECATED)
        }
        if (isAnnotationPresent(ApiStatus.Internal::class.java)) {
            add(SourceCodeSymbolFlag.INTERNAL)
        }
        if (isAnnotationPresent(ApiStatus.Experimental::class.java)) {
            add(SourceCodeSymbolFlag.EXPERIMENTAL)
        }
    }
}

fun NodeWithAnnotations<*>.getApiStatusAvailableSince(): Optional<String> {
    return getAnnotationByClass(ApiStatus.AvailableSince::class.java)
        .filter { it.isSingleMemberAnnotationExpr }
        .map { it.asSingleMemberAnnotationExpr().memberValue.asStringLiteralExpr().asString() }
}
