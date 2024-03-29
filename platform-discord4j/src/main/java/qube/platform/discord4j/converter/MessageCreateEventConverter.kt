package qube.platform.discord4j.converter

import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.entity.Message
import qube.core.event.domain.QubeChannel
import qube.core.event.domain.QubeInstance
import qube.core.event.domain.QubeMessage
import qube.core.event.domain.QubeMessageActionType
import qube.core.event.domain.QubeUser
import qube.core.event.events.QubeMessageEvent
import java.net.URI

fun MessageCreateEvent.convertToQubeMessage(): QubeMessageEvent {
    return QubeMessageEvent(
        eventSource = URI.create("/discord/${guildId.get().asString()}"),
        instance = QubeInstance("discord", guildId.get().asString()),
        channel = QubeChannel(id = message.channelId.asString(), name = null),
        user = QubeUser(
            id = message.author.map { a -> a.id.asString() }.orElse(null),
            name = message.author.map { a -> a.username }.orElse(null),
            // TODO: use nickname from member if available
            displayName = message.author.map { a -> a.username }.orElse(null),
            avatarUrl = message.author.map { a -> a.avatarUrl }.orElse(null),
            mention = message.author.map { a -> a.mention }.orElse(null),
            bot = message.author.map { a -> a.isBot }.orElse(false)
        ),
        message = QubeMessage(
            id = message.id.asString(),
            text = message.content,
            responseTo = message.referencedMessage.map { referencedMessage: Message ->
                referencedMessage.id.asString()
            }.orElse(null)
        ),
        action = QubeMessageActionType.CREATE
    )
}
