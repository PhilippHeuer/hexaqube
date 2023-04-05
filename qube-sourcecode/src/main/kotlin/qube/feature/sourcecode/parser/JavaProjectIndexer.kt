package qube.feature.sourcecode.parser

import com.github.javaparser.JavaParser
import com.github.javaparser.ParserConfiguration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.ObjectCreationExpr
import com.github.javaparser.resolution.TypeSolver
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver
import io.github.oshai.KotlinLogging
import qube.feature.sourcecode.domain.SourceCodeSymbol
import qube.feature.sourcecode.domain.SourceCodeSymbolFlag
import qube.feature.sourcecode.extension.getApiStatusAvailableSince
import qube.feature.sourcecode.extension.getFlags
import qube.feature.sourcecode.extension.getJavadocDescription
import qube.feature.sourcecode.extension.getJavadocSince
import qube.feature.sourcecode.extension.parentObjectName
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path
import java.util.stream.Collectors
import kotlin.Boolean
import kotlin.IllegalStateException
import kotlin.io.path.absolutePathString
import kotlin.to

private val logger = KotlinLogging.logger {}

class JavaProjectIndexer(
    private val projectDirectory: Path,
): ProjectIndexer {
    private var typeSolver: TypeSolver = CombinedTypeSolver(JavaParserTypeSolver(projectDirectory.absolutePathString()))
    private var javaParser: JavaParser

    init {
        val parserConfiguration = ParserConfiguration()
        parserConfiguration.setSymbolResolver(JavaSymbolSolver(typeSolver))
        javaParser = JavaParser(parserConfiguration)
    }

    /**
     * indexer supports java files
     */
    override fun supportsFile(file: File): Boolean {
        return "java".equals(file.extension, ignoreCase = true)
    }

    override fun indexFile(file: File): List<SourceCodeSymbol> {
        logger.trace { "processing file: ${file.absolutePath}" }
        val symbols = mutableListOf<SourceCodeSymbol>()

        try {
            val cu = javaParser.parse(file).result.orElseThrow { IllegalStateException("could not parse file: ${file.absolutePath}") }
            val allClasses = cu.findAll(ClassOrInterfaceDeclaration::class.java).stream().collect(Collectors.toUnmodifiableList())
            val allMethods = cu.findAll(MethodDeclaration::class.java).stream().collect(Collectors.toUnmodifiableList())

            allClasses.forEach { clazz ->
                val fullClassName = clazz.fullyQualifiedName.orElse(clazz.nameAsString)
                val namespace = fullClassName.substring(0, fullClassName.lastIndexOf("."))
                val className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
                logger.trace { "processing class: ${namespace}.${className}" }

                symbols.add(SourceCodeSymbol(
                    selector = "${namespace}.${className}",
                    type = "class",
                    namespace = namespace,
                    name = className,
                    description = clazz.getJavadocDescription().orElse(null),
                    file = file.absolutePath.substring(projectDirectory.absolutePathString().length + 1).replace("\\", "/"),
                    lineBegin = clazz.begin.get().line,
                    lineEnd = clazz.end.get().line,
                    length = clazz.end.get().line - clazz.begin.get().line,
                    addedIn = clazz.getApiStatusAvailableSince().orElse(clazz.getJavadocSince().orElse(null)),
                    flags = SourceCodeSymbolFlag.ofSet(clazz.getFlags()),
                    properties = mapOf(
                        "namespace" to namespace,
                    ),
                    definition = "",
                ))
            }
            allMethods.forEach { method ->
                val parentObjectName = method.parentObjectName()
                logger.trace { "processing method: ${parentObjectName}.${method.nameAsString}" }

                // ignore methods that are part of an object creation expression
                if (method.parentNode.orElse(null) is ObjectCreationExpr) {
                    return@forEach
                }

                // build parameter string for unique identifier
                val paramStr = method.parameters.joinToString(separator = ", ") {
                    "${it.type} ${it.name}"
                }
                val namespace = parentObjectName.substring(0, parentObjectName.lastIndexOf("."))
                val className = parentObjectName.substring(parentObjectName.lastIndexOf(".") + 1)

                // method parameters
                val parameters = method.parameters.mapIndexed { index, param ->
                    mapOf(
                        "order" to index,
                        "type" to param.typeAsString,
                        "name" to param.nameAsString,
                        "description" to method.javadoc.map { doc ->
                            doc.blockTags.find { tag -> tag.tagName == "param" && tag.name.orElse(null) == param.nameAsString }?.content?.toText()?.trim()
                        }.orElse(null),
                    )
                }.toList()

                symbols.add(SourceCodeSymbol(
                    selector = "${namespace}.${className}#${method.nameAsString}(${paramStr})",
                    type = "method",
                    namespace = namespace,
                    name = "${className}#${method.nameAsString}",
                    description = method.getJavadocDescription().orElse(null),
                    file = file.absolutePath.substring(projectDirectory.absolutePathString().length + 1).replace("\\", "/"),
                    lineBegin = method.begin.get().line,
                    lineEnd = method.end.get().line,
                    length = method.end.get().line - method.begin.get().line,
                    addedIn = method.getApiStatusAvailableSince().orElse(method.getJavadocSince().orElse(null)),
                    properties = mapOf(
                        "namespace" to namespace,
                        "class_name" to className,
                        "return_type" to method.type.asString(),
                        "parameters" to parameters,
                    ),
                    flags = SourceCodeSymbolFlag.ofSet(method.getFlags()),
                    definition = "",
                ))
            }
        } catch (ex: FileNotFoundException) {
            // can't happen
        }

        return symbols
    }

}
