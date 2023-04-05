package qube.core.command

import io.github.oshai.KotlinLogging
import io.quarkus.runtime.Startup
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Instance
import org.eclipse.microprofile.config.ConfigProvider

@Startup
@ApplicationScoped
class CommandManager(
    commandInstances: Instance<QubeCommand>,
) {
    val commands = commandInstances.filter { ConfigProvider.getConfig().getOptionalValue("command.${it.name()}.enabled", Boolean::class.java).orElse(false) }.associateBy { it.name() }.toMap()

    init {
        logger.info { "registered commands: ${commands.keys} (${commands.size}/${commandInstances.asIterable().toList().size})" }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
