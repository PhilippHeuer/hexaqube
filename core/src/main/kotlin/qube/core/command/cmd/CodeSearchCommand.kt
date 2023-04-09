package qube.core.command.cmd

import jakarta.enterprise.context.ApplicationScoped
import qube.core.command.domain.QubeCommand
import qube.core.command.domain.QubeCommandFlags
import qube.core.command.domain.QubeCommandOption
import qube.core.command.domain.QubeCommandParameter

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
                name = "annotations",
                description = "filter by annotations",
                type = String::class.java,
                default = null,
                options = listOf(
                    QubeCommandOption(
                        name = "deprecated",
                        description = "search for deprecated methods",
                    ),
                    QubeCommandOption(
                        name = "internal",
                        description = "search for internal methods",
                    ),
                    QubeCommandOption(
                        name = "experimental",
                        description = "search for experimental methods",
                    ),
                ),
                required = false,
            )
        )
    }

    override fun flags(): Set<QubeCommandFlags> {
        return emptySet()
    }

}
