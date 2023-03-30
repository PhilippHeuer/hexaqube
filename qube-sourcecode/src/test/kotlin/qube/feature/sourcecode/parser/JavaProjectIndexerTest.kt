package qube.feature.sourcecode.parser

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Path

class JavaProjectIndexerTest {

    @Test
    fun indexTestFile() {
        // init with source dir set the test resources
        val indexer = JavaProjectIndexer(projectDirectory = Path.of("src/test/resources"))
        val symbols = indexer.indexFile(File("src/test/resources/parser/java/class.java"))
        // println(symbols)

        // class
        symbols[0].let {
            Assertions.assertEquals("com.qube.HelloWorld", it.selector)
            Assertions.assertEquals("class", it.type)
            Assertions.assertEquals("com.qube", it.namespace)
            Assertions.assertEquals("HelloWorld", it.name)
            Assertions.assertEquals(1, it.flags)
        }

        // method
        symbols[1].let {
            Assertions.assertEquals("com.qube.HelloWorld#helloWorld()", it.selector)
            Assertions.assertEquals("method", it.type)
            Assertions.assertEquals("com.qube", it.namespace)
            Assertions.assertEquals("HelloWorld#helloWorld", it.name)
            Assertions.assertEquals("1.1.0", it.addedIn)
            Assertions.assertEquals(1, it.flags)
        }
        symbols[2].let {
            Assertions.assertEquals("com.qube.HelloWorld#aMethod()", it.selector)
            Assertions.assertEquals("method", it.type)
            Assertions.assertEquals("com.qube", it.namespace)
            Assertions.assertEquals("HelloWorld#aMethod", it.name)
            Assertions.assertEquals("0.0.1", it.addedIn)
            Assertions.assertEquals(1, it.flags)
        }
        symbols[3].let {
            Assertions.assertEquals("com.qube.HelloWorld#goodbye()", it.selector)
            Assertions.assertEquals("method", it.type)
            Assertions.assertEquals("com.qube", it.namespace)
            Assertions.assertEquals("HelloWorld#goodbye", it.name)
            Assertions.assertEquals(null, it.addedIn)
            Assertions.assertEquals(15, it.flags)
        }
    }

}
