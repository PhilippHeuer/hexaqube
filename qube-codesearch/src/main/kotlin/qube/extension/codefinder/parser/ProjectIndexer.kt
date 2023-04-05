package qube.extension.codefinder.parser

import qube.extension.codefinder.parser.domain.SourceCodeSymbol
import java.io.File

interface ProjectIndexer {

    fun supportsFile(file: File): Boolean

    fun indexFile(file: File): List<SourceCodeSymbol>

}
