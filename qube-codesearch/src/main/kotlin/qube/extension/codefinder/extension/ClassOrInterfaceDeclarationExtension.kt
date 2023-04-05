package qube.extension.codefinder.extension

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration

/**
 * generates a declaration snippet for a class
 */
fun ClassOrInterfaceDeclaration.getDeclaration(): String {
    val visibility = when {
        isPublic -> "public"
        isPrivate -> "private"
        isProtected -> "protected"
        else -> ""
    }
    val type = when {
        isInterface -> "interface"
        isAbstract -> "abstract class"
        isEnumDeclaration || isEnumConstantDeclaration -> "enum"
        else -> "class"
    }
    val implements = implementedTypes.takeIf { it.isNotEmpty() }?.joinToString(", ", " implements ") { it.nameAsString } ?: ""
    val extends = extendedTypes.takeIf { it.isNotEmpty() }?.joinToString(", ", " extends ") { it.nameAsString } ?: ""

    return "$visibility $type $nameAsString()$implements$extends"
}
