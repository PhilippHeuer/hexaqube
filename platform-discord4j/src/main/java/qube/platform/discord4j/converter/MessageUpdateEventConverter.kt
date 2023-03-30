package qube.platform.discord4j.converter

import discord4j.core.event.domain.message.MessageUpdateEvent
import discord4j.core.`object`.entity.Message
import qube.core.eventbus.domain.QubeChannel
import qube.core.eventbus.domain.QubeInstance
import qube.core.eventbus.domain.QubeMessage
import qube.core.eventbus.domain.QubeMessageActionType
import qube.core.eventbus.domain.QubeUser
import qube.core.eventbus.events.QubeMessageEvent
import java.net.URI

fun MessageUpdateEvent.convertToQubeMessage(): QubeMessageEvent {
    return message.map { msg ->
        return@map QubeMessageEvent(
            eventSource = URI.create("/discord/${guildId.get().asString()}"),
            eventType = "qube.message.update",
            instance = QubeInstance("discord", guildId.get().asString()),
            channel = QubeChannel(
                id = channelId.asString(),
                name = ""
            ),
            user = QubeUser(
                id = msg.author.map { a -> a.id.asString() }.orElse(null),
                name = msg.author.map { a -> a.username }.orElse(null),
                // TODO: use nickname from member if available
                displayName = msg.author.map { a -> a.username }.orElse(null),
                avatarUrl = msg.author.map { a -> a.avatarUrl }.orElse(null),
                mention = msg.author.map { a -> a.mention }.orElse(null),
                bot = msg.author.map { a -> a.isBot }.orElse(false)
            ),
            message = QubeMessage(
                id = messageId.asString(),
                text = msg.content,
                responseTo = msg.referencedMessage.map { referencedMessage: Message ->
                    referencedMessage.id.asString()
                }.orElse(null)
            ),
            action = QubeMessageActionType.UPDATE
        )
    }.blockOptional().orElseThrow()
}
