package qube.extension.codefinder.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RepositoryUtilsTest {

    @Test
    fun testGetReferences() {
        val ref = RepositoryUtils.getRepositoryReferences("https://github.com/twitch4j/twitch4j.git").stream().filter { it.name == "refs/tags/v1.0.0" }.findFirst().orElseThrow()
        Assertions.assertEquals("refs/tags/v1.0.0", ref.name)
        Assertions.assertEquals("62a2787298a00f079da34d637c091bc4a6ad332e", ref.objectId.name)
    }

}
