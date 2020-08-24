package me.ufo.rift.redis;

import java.io.Closeable;
import java.time.Duration;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import me.ufo.rift.Rift;

public final class Redis implements Closeable {

    private final Rift plugin;
    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> connection;
    private final StatefulRedisPubSubConnection<String, String> psConnection;

    public Redis(final Rift plugin) {
        this.plugin = plugin;
        this.redisClient = RedisClient.create(this.credentials());
        this.connection = this.redisClient.connect();
        this.psConnection = this.redisClient.connectPubSub();
        this.psConnection.addListener(new PubSubListener(this.plugin));
        this.psConnection.async().subscribe(
            "rift:" + this.plugin.name()
        );
    }

    public RedisFuture<Long> async(final String destination, final String action, final String message) {
        if (this.plugin.debug()) {
            this.plugin.info("Publish async: (" + destination + ", " + action + ", " + message + ")");
        }

        if (this.connection == null) {
            this.plugin.severe("Async connection null");
            return null;
        }

        return this.connection.async().publish(
            "rift:" + destination, this.plugin.name() + "," + action + "," + message
        );
    }

    public Long sync(final String destination, final String action, final String message) {
        if (this.plugin.debug()) {
            this.plugin.info("Publish sync: (" + destination + ", " + action + ", " + message + ")");
        }

        if (this.connection == null) {
            this.plugin.severe("Sync connection null");
            return -1L;
        }

        return this.connection.sync().publish(
            "rift:" + destination, this.plugin.name() + "," + action + "," + message
        );
    }

    private RedisURI credentials() {
        final RedisURI credentials = new RedisURI(
            this.plugin.getConfig().getString("redis.host"),
            this.plugin.getConfig().getInt("redis.port"),
            Duration.ofSeconds(30)
        );

        if (this.plugin.getConfig().getBoolean("redis.auth.enabled")) {
            credentials.setPassword(this.plugin.getConfig().getString("redis.auth.password"));
        }

        return credentials;
    }

    @Override
    public void close() {
        this.connection.close();
        this.psConnection.close();
        this.redisClient.shutdown();
    }

}
