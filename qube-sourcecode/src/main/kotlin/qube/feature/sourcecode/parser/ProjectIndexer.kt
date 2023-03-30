package qube.feature.sourcecode.parser

import qube.feature.sourcecode.domain.SourceCodeSymbol
import java.io.File

interface ProjectIndexer {

    fun supportsFile(file: File): Boolean

    fun indexFile(file: File): List<SourceCodeSymbol>

}
