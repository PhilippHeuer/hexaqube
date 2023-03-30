package qube.feature.sourcecode.extension

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.EnumConstantDeclaration
import com.github.javaparser.ast.body.EnumDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.ObjectCreationExpr

fun MethodDeclaration.parentObjectName(): String {
    return parentNode.map {
        when (it) {
            is ClassOrInterfaceDeclaration -> it.fullyQualifiedName.orElse(it.nameAsString)
            is EnumDeclaration -> it.fullyQualifiedName.orElse(it.nameAsString)
            is EnumConstantDeclaration -> it.nameAsString
            is ObjectCreationExpr -> it.typeAsString
            else -> throw IllegalStateException("unknown parent type: ${it.javaClass}")
        }
    }.get()
}
