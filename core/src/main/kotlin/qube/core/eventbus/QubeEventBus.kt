package qube.core.eventbus

import io.cloudevents.CloudEvent
import org.eclipse.microprofile.reactive.messaging.*
import qube.core.eventbus.domain.QubeMessageActionType
import qube.core.eventbus.events.QubeMessageEvent
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class QubeEventBus(
    @Channel("out-message-createupdate")
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 500)
    private var messageCreateUpdateEmitter: Emitter<CloudEvent>,

    @Channel("out-message-create")
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 500)
    private var messageCreateEmitter: Emitter<CloudEvent>,

    @Channel("out-message-update")
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 500)
    private var messageUpdateEmitter: Emitter<CloudEvent>,
) {
    fun publish(payload: QubeMessageEvent) {
        val event = payload.toCloudEvent()

        messageCreateUpdateEmitter.send(event)
        when(payload.action) {
            QubeMessageActionType.CREATE -> messageCreateEmitter.send(event)
            QubeMessageActionType.UPDATE -> messageUpdateEmitter.send(event)
            else -> {}
        }
    }
}
