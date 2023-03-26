package qube.core.eventbus.extensions

import io.cloudevents.core.v1.CloudEventV1

fun CloudEventV1.toLogString(channel: String): String {
    return "Event(id=${this.id}, schema=${this.dataSchema}, channel=$channel)"
}
