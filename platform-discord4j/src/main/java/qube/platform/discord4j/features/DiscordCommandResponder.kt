package qube.platform.discord4j.features

import discord4j.core.spec.EmbedCreateFields
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.InteractionReplyEditSpec
import discord4j.rest.util.Color
import io.cloudevents.core.v1.CloudEventV1
import io.github.oshai.KotlinLogging
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.infrastructure.Infrastructure
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.context.control.ActivateRequestContext
import org.eclipse.microprofile.reactive.messaging.Incoming
import qube.core.event.events.QubeCommandResponseEvent
import qube.core.event.extensions.toLogString
import qube.core.template.TemplateEngine
import qube.core.util.QubeObjectMapper
import qube.platform.discord4j.DiscordPlatform

@ApplicationScoped
class DiscordCommandResponder(
    private val platform: DiscordPlatform,
    private val templateEngine: TemplateEngine
) {
    @Incoming("discord-command-response")
    @ActivateRequestContext
    fun consume(cloudEvent: CloudEventV1): Uni<Void> {
        logger.debug { "event received: ${cloudEvent.toLogString()}"}

        return Uni.createFrom().item(cloudEvent)
            .map { QubeObjectMapper.json.readValue(it.data?.toBytes() ?: ByteArray(0), QubeCommandResponseEvent::class.java) }
            .emitOn(Infrastructure.getDefaultWorkerPool())
            .onItem().invoke { event ->
                // render template
                val output = templateEngine.render(event.templateId, event.instance, event.templateData)

                // send response (embed or plain text based on the template)
                val interaction = platform.commandCache.remove(event.command.commandId)
                if (output.title.isNotBlank()) {
                    interaction?.editReply(
                        InteractionReplyEditSpec.builder()
                            .build()
                            .withEmbeds(EmbedCreateSpec.create()
                                .withColor(Color.CYAN)
                                .withTitle(output.title)
                                .withDescription(output.content)
                                .withFooter(EmbedCreateFields.Footer.of(output.footer, null))
                            )
                    )?.subscribe()
                } else {
                    interaction?.editReply(
                        InteractionReplyEditSpec.builder()
                            .build()
                            .withContentOrNull(output.content)
                    )?.subscribe()
                }
            }
            .onItem().ignore().andContinueWithNull()
            .onFailure().invoke { ex: Throwable ->
                logger.error(ex) { "event processing failed: ${cloudEvent.toLogString()}. Error: ${ex.message}" }
            }
            .onItem().invoke { _ -> logger.info { "event processed successfully: ${cloudEvent.toLogString()}"} }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
