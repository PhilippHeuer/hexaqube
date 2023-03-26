package qube.platform.discord4j.health

import org.eclipse.microprofile.health.HealthCheck
import org.eclipse.microprofile.health.HealthCheckResponse
import org.eclipse.microprofile.health.Liveness
import qube.platform.discord4j.Discord4JPlatform
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
@Liveness
class Discord4JPlatformHealth(
    private val platform: Discord4JPlatform
) : HealthCheck {

    override fun call(): HealthCheckResponse? {
        val shardId = 0
        val shardCount = platform.gateway.gatewayClientGroup.shardCount
        val connected = platform.gateway.getGatewayClient(shardId).get().isConnected.block()!!

        val response = HealthCheckResponse
            .builder()
            .withData("shard_count", shardCount.toString())
            .withData("shard_id", shardId.toString())
            .name("platform.discord4j.connection")

        return if (connected) {
            response.up().build()
        } else {
            response.down().build()
        }
    }
}
