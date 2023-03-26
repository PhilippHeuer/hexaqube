package qube.core.exception

class EventException(override val message: String?, override val cause: Throwable?): RuntimeException(message, cause) {
}
