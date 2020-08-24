package me.ufo.rift.redis;

import java.io.Closeable;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
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
        this.redisClient = RedisClient.create(this.plugin.config().credentials());
        this.connection = this.redisClient.connect();
        this.psConnection = this.redisClient.connectPubSub();
        this.psConnection.addListener(new PubSubListener(this.plugin));
        this.psConnection.async().subscribe(
            "rift:bungee",
            "rift:all"
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
            "rift:" + destination, "bungee" + "," + action + "," + message
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
            "rift:" + destination, "bungee" + "," + action + "," + message
        );
    }

    @Override
    public void close() {
        this.connection.close();
        this.psConnection.close();
        this.redisClient.shutdown();
    }

}
