package qube.codefinder.parser

import qube.codefinder.parser.domain.SourceCodeSymbol
import java.io.File

interface ProjectIndexer {

    fun supportsFile(file: File): Boolean

    fun indexFile(file: File): List<SourceCodeSymbol>

}
