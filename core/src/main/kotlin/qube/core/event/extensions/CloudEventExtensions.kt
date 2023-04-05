package qube.core.event.extensions

import io.cloudevents.core.v1.CloudEventV1

fun CloudEventV1.toLogString(): String {
    return "Event(id=${this.id}, schema=${this.dataSchema})"
}
