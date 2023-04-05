package qube.core.config

import io.github.oshai.KotlinLogging
import io.quarkus.runtime.Startup
import jakarta.enterprise.context.ApplicationScoped
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.common.config.TopicConfig
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.time.Duration
import java.util.Collections
import java.util.Properties

/**
 * Creates and configures topics on startup.
 */
@Startup
@ApplicationScoped
class TopicManager(
    @ConfigProperty(name = "kafka.bootstrap.servers")
    private val bootstrapServers: String
) {
    init {
        // platform events
        createTopic("qube.message.create")
        createTopic("qube.message.update")

        // commands
        createTopic("qube.command.execute.codesearch")

        // platform actions
        createTopic("qube.discord.command.response")
    }

    private fun createTopic(
        topicName: String,
        numPartitions: Int = 1,
        replicationFactor: Short = 1.toShort(),
        retention: Duration = Duration.ofMinutes(5)
    ) {
        val props = Properties()
        props[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers

        val topicConfig = HashMap<String, String>()
        topicConfig[TopicConfig.CLEANUP_POLICY_CONFIG] = TopicConfig.CLEANUP_POLICY_DELETE
        topicConfig[TopicConfig.DELETE_RETENTION_MS_CONFIG] = retention.toMillis().toString()
        topicConfig[TopicConfig.COMPRESSION_TYPE_CONFIG] = "lz4"

        try {
            AdminClient.create(props).use { adminClient ->
                val topics = adminClient.listTopics()
                if (topics.names().get().contains(topicName)) {
                    logger.debug { "topic already exists: $topicName" }
                    return
                }

                val newTopic = NewTopic(topicName, numPartitions, replicationFactor)
                newTopic.configs(topicConfig)
                adminClient.createTopics(Collections.singleton(newTopic)).all().get()
            }

            logger.info { "created new topic: $topicName" }
        } catch (e: Exception) {
            logger.error(e) { "failed to create topic: $topicName" }
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
