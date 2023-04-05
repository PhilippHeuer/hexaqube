package qube.core.command.cmd

import jakarta.enterprise.context.ApplicationScoped
import qube.core.command.QubeCommand
import qube.core.command.QubeCommandFlags
import qube.core.command.QubeCommandParameter

@ApplicationScoped
class CodeSearchCommand: QubeCommand {
    override fun name(): String {
        return "codesearch"
    }

    override fun aliases(): List<String> {
        return emptyList()
    }

    override fun description(): String {
        return "searches the source code index for methods or classes"
    }

    override fun parameters(): List<QubeCommandParameter<*>> {
        return listOf(
            QubeCommandParameter(
                name = "query",
                description = "search query",
                type = String::class.java,
                default = null,
                options = emptyList(),
                required = true,
            ),
            QubeCommandParameter(
                name = "version",
                description = "method needs to be present in version",
                type = String::class.java,
                default = null,
                options = emptyList(),
                required = false,
            )
        )
    }

    override fun flags(): Set<QubeCommandFlags> {
        return emptySet()
    }

}
