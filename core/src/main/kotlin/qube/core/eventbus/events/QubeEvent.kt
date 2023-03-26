package qube.core.eventbus.events

import java.net.URI

open interface QubeEvent {
    val source: URI
}
