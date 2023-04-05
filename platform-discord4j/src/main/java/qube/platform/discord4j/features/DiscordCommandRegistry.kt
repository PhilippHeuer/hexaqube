package qube.platform.discord4j.features

import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.command.ApplicationCommandOption
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandRequest
import jakarta.enterprise.context.ApplicationScoped
import qube.core.command.CommandManager

@ApplicationScoped
class DiscordCommandRegistry(
    private var commandManager: CommandManager
) {

    /**
     * Update discord slash command list for a guild
     */
    fun updateGuildCommands(gateway: GatewayDiscordClient, guildId: Long) {
        // replace guild commands
        commandManager.commands.map {
            val builder = ApplicationCommandRequest.builder()
            builder.name(it.value.name())
            builder.description(it.value.description())

            builder.addAllOptions(it.value.parameters().map { param ->
                val type = when (param.type.javaClass) {
                    String::javaClass -> ApplicationCommandOption.Type.STRING.value
                    Int::javaClass -> ApplicationCommandOption.Type.INTEGER.value
                    Boolean::javaClass -> ApplicationCommandOption.Type.BOOLEAN.value
                    else -> ApplicationCommandOption.Type.STRING.value
                }

                ApplicationCommandOptionData.builder()
                    .name(param.name)
                    .description(param.description)
                    .type(type)
                    .required(param.required)
                    .build()
            })

            builder.build()
        }.let { commandList ->
            val applicationId = gateway.restClient.applicationId.block()
            gateway.restClient.applicationService
                .bulkOverwriteGuildApplicationCommand(applicationId!!, guildId, commandList)
                .subscribe()
        }
    }

}
