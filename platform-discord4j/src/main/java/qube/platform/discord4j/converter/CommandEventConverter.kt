package qube.platform.discord4j.converter

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import qube.core.event.domain.QubeInstance
import qube.core.event.events.QubeCommandEvent
import java.net.URI
import java.time.Instant

fun ApplicationCommandInteractionEvent.convertToQubeCommand(): QubeCommandEvent {
    return QubeCommandEvent(
            eventSource = URI.create("/discord/${interaction.guildId.map { it.asString() }.orElse("DM")}/${shardInfo.index}"),
            instance = QubeInstance("discord", interaction.guildId.map { it.asString() }.orElse("DM")),
            commandId = interaction.id.asString(),
            commandName = commandName,
            parameters = interaction.commandInteraction.map { ci -> ci.options.associate { opt -> opt.name to opt.value.get().asString() }}.orElse(emptyMap()),
            createdAt = Instant.now()
        )
}
