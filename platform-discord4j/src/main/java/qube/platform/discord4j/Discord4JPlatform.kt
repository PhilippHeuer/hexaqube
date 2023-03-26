package qube.platform.discord4j

import discord4j.core.event.domain.Event as DiscordEvent
import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.event.domain.message.MessageUpdateEvent
import discord4j.core.`object`.presence.Activity
import discord4j.core.`object`.presence.ClientActivity
import discord4j.core.`object`.presence.ClientPresence
import discord4j.core.`object`.presence.Status
import discord4j.gateway.intent.Intent
import discord4j.gateway.intent.IntentSet
import io.github.oshai.KotlinLogging
import io.micrometer.core.annotation.Counted
import io.quarkus.runtime.Startup
import org.eclipse.microprofile.config.inject.ConfigProperty
import qube.core.eventbus.QubeEventBus
import qube.platform.discord4j.converter.convertToQubeMessage
import reactor.util.retry.Retry
import java.time.Duration
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.enterprise.context.ApplicationScoped

private val logger = KotlinLogging.logger {}


@Startup
@ApplicationScoped
class Discord4JPlatform(
    @ConfigProperty(name = "platform.discord4j.token") private val token: String,
    private var events: QubeEventBus
) {
    private val client: DiscordClient = DiscordClient.create(token)
    lateinit var gateway: GatewayDiscordClient

    @PostConstruct
    fun init() {
        if (token.isEmpty()) {
            logger.warn { "[discord4j] platform will be disabled, no auth token provided" }
            return
        }

        logger.info { "[discord4j] connecting ..." }
        client.gateway()
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
            .doOnSuccess { loggedInGateway ->
                logger.info { "[discord4j] connected" }

                // register event forwarder
                loggedInGateway.on(DiscordEvent::class.java).subscribe { event ->
                    processDiscordEvent(event)
                }

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
        gateway.logout().block()
    }

    @Counted(value = "platform.discord4j.events.received", description = "the total count of events received from discord4j")
    fun processDiscordEvent(event: DiscordEvent) {
        if (event is MessageCreateEvent) {
            events.publish(event.convertToQubeMessage())
        } else if (event is MessageUpdateEvent) {
            events.publish(event.convertToQubeMessage())
        }
    }
}
