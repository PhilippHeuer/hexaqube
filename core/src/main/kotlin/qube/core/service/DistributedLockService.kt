package qube.core.service

import io.etcd.jetcd.Client
import net.javacrumbs.shedlock.core.LockConfiguration
import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.core.SimpleLock
import net.javacrumbs.shedlock.provider.etcd.jetcd.EtcdLockProvider
import net.javacrumbs.shedlock.provider.inmemory.InMemoryLockProvider
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.time.Duration
import java.time.Instant
import java.util.Optional
import javax.enterprise.context.ApplicationScoped

/**
 * Service for handling distributed locks
 */
@ApplicationScoped
class DistributedLockService(
    @ConfigProperty(name = "etcd.enabled") private val enabled: String,
    @ConfigProperty(name = "etcd.host") private val host: String,
) {
    private val lockProvider: LockProvider = if (enabled.toBoolean()) {
        val client = Client.builder().endpoints(host).build()
        EtcdLockProvider(client)
    } else {
        InMemoryLockProvider()
    }

    /**
     * Acquire a distributed lock
     *
     * @param name the name of the lock
     * @param atLeastFor the minimum time the lock should be held
     * @param atMostFor the maximum time the lock should be held
     * @return the lock if it was acquired, empty otherwise
     */
    fun getLock(name: String, atLeastFor: Duration, atMostFor: Duration): Optional<SimpleLock> {
        return lockProvider.lock(LockConfiguration(Instant.now(), "lock", atMostFor, atLeastFor))
    }
}
