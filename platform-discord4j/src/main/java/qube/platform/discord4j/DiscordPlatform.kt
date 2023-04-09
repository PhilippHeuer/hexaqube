package qube.platform.discord4j

import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.guild.GuildCreateEvent
import discord4j.core.event.domain.guild.GuildDeleteEvent
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.event.domain.message.MessageUpdateEvent
import discord4j.core.`object`.presence.Activity
import discord4j.core.`object`.presence.ClientActivity
import discord4j.core.`object`.presence.ClientPresence
import discord4j.core.`object`.presence.Status
import discord4j.core.shard.ShardingStrategy
import discord4j.gateway.intent.Intent
import discord4j.gateway.intent.IntentSet
import io.cloudevents.CloudEvent
import io.github.oshai.KotlinLogging
import io.quarkus.runtime.Startup
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.eclipse.microprofile.reactive.messaging.OnOverflow
import qube.platform.discord4j.converter.convertToQubeCommand
import qube.platform.discord4j.converter.convertToQubeMessage
import qube.platform.discord4j.features.DiscordCommandRegistry
import reactor.core.scheduler.Schedulers
import reactor.util.retry.Retry
import java.time.Duration

private val logger = KotlinLogging.logger {}

@Startup
@ApplicationScoped
class DiscordPlatform(
    @Channel("discord-message-create")
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 500)
    private var messageCreateEmitter: Emitter<CloudEvent>,

    @Channel("discord-message-update")
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 500)
    private var messageUpdateEmitter: Emitter<CloudEvent>,

    @Channel("discord-command-execute")
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 500)
    private var commandEmitter: Emitter<CloudEvent>,

    private var commandRegistry: DiscordCommandRegistry,

    @ConfigProperty(name = "platform.discord4j.token")
    private val token: String,
) {
    private val client: DiscordClient = DiscordClient.create(token)
    lateinit var gateway: GatewayDiscordClient
    val commandCache = mutableMapOf<String, ApplicationCommandInteractionEvent>()

    @PostConstruct
    fun init() {
        if (token.isEmpty()) {
            logger.warn { "[discord4j] platform will be disabled, no auth token provided" }
            return
        }

        logger.info { "[discord4j] connecting ..." }
        client.gateway()
            .setSharding(ShardingStrategy.recommended())
            .setEnabledIntents(
                IntentSet.of(
                    Intent.DIRECT_MESSAGES,
                    Intent.GUILDS,
                    Intent.GUILD_MEMBERS,
                    Intent.GUILD_MESSAGES,
                    Intent.GUILD_VOICE_STATES,
                    Intent.GUILD_MESSAGE_REACTIONS,
                    Intent.GUILD_WEBHOOKS,
                    Intent.GUILD_PRESENCES,
                    Intent.GUILD_EMOJIS,
                    Intent.GUILD_INTEGRATIONS,
                    Intent.GUILD_INVITES,
                )
            )
            .login()
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(10)))
            .flatMap { loggedInGateway ->
                loggedInGateway.updatePresence(
                    ClientPresence.of(
                        Status.ONLINE,
                        ClientActivity.of(Activity.Type.CUSTOM, "loading ...", null)
                    ), 0
                )
                .thenReturn(loggedInGateway)
            }
            .publishOn(Schedulers.immediate())
            .doOnSuccess { loggedInGateway ->
                logger.info { "[discord4j] connected" }

                // register subscriptions
                loggedInGateway.on(GuildCreateEvent::class.java).subscribe { onInstanceConnect(it) }
                loggedInGateway.on(GuildDeleteEvent::class.java).subscribe { onInstanceDisconnect(it) }
                loggedInGateway.on(MessageCreateEvent::class.java).subscribe { onMessageCreate(it) }
                loggedInGateway.on(MessageUpdateEvent::class.java).subscribe { onMessageUpdate(it) }
                loggedInGateway.on(ApplicationCommandInteractionEvent::class.java).subscribe { onSlashCommand(it) }

                gateway = loggedInGateway
            }
            .doOnError {
                logger.error(it) { "[discord4j] connection failed" }
            }
            .block()!!
    }

    @PreDestroy
    fun destroy() {
        logger.info { "[discord4j] disconnecting ..." }

        // cancel command replies
        commandCache.forEach {
            it.value.deleteReply().block(Duration.ofMillis(100))
        }

        // disconnect
        gateway.logout().block()
    }

    private fun onInstanceConnect(event: GuildCreateEvent) {
        logger.info { "[discord4j] instance connected: ${event.guild.name}" }
        commandRegistry.updateGuildCommands(gateway, event.guild.id.asLong())
    }

    private fun onInstanceDisconnect(event: GuildDeleteEvent) {
        logger.info { "[discord4j] instance disconnected: ${event.guildId.asString()}" }
    }

    private fun onMessageCreate(event: MessageCreateEvent) {
        val msg = event.convertToQubeMessage()
        messageCreateEmitter.send(msg.toEventMessage())
    }

    private fun onMessageUpdate(event: MessageUpdateEvent) {
        val msg = event.convertToQubeMessage()
        messageUpdateEmitter.send(msg.toEventMessage())
    }

    private fun onSlashCommand(event: ApplicationCommandInteractionEvent) {
        val msg = event.convertToQubeCommand()
        commandCache[msg.commandId] = event
        commandEmitter.send(msg.toEventMessage(topic = "qube.command.execute.${msg.commandName}"))

        // reply later
        event.deferReply().subscribe()
    }
}
