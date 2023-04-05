package qube.platform.discord4j.health

import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.health.HealthCheck
import org.eclipse.microprofile.health.HealthCheckResponse
import org.eclipse.microprofile.health.Liveness
import qube.platform.discord4j.DiscordPlatform

@ApplicationScoped
@Liveness
class DiscordPlatformHealth(
    private val platform: DiscordPlatform
) : HealthCheck {

    override fun call(): HealthCheckResponse? {
        val shardCount = platform.gateway.gatewayClientGroup.shardCount
        val response = HealthCheckResponse
            .builder()
            .withData("shard_count", shardCount.toString())
            .name("platform.discord4j.connection")

        var connectedCount = 0
        for (i in 0..shardCount) {
            val shard = platform.gateway.getGatewayClient(i).get()
            val shardConnected = shard.isConnected.block()!!
            response.withData("shard_$i", shardConnected.toString())
            if (shardConnected) {
                connectedCount += 1
            }
        }

        return if (connectedCount > 0) {
            response.up().build()
        } else {
            response.down().build()
        }
    }
}
