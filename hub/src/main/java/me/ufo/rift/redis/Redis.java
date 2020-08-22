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
        this.redisClient = RedisClient.create("redis://localhost");
        this.connection = this.redisClient.connect();
        this.psConnection = this.redisClient.connectPubSub();

        this.psConnection.addListener(new PubSubListener(this.plugin));
        this.psConnection.async().subscribe("rift:" + this.plugin.name());
    }

    public RedisFuture<Long> async(final String destination, final String channel, final String message) {
        this.plugin.debug("Publish async: (" + destination + ", " + channel + ", " + message + ")");

        if (this.connection == null) {
            this.plugin.severe("Async connection null");
            return null;
        }

        return this.connection.async().publish(
            "rift:" + destination, this.plugin.name() + "," + channel + "," + message
        );
    }

    public Long sync(final String destination, final String channel, final String message) {
        this.plugin.debug("Publish sync: (" + destination + ", " + channel + ", " + message + ")");

        if (this.connection == null) {
            this.plugin.severe("Sync connection null");
            return -1L;
        }

        return this.connection.sync().publish(
            "rift:" + destination, this.plugin.name() + "," + channel + "," + message
        );
    }

    @Override
    public void close() {
        this.connection.close();
        this.psConnection.close();
        this.redisClient.shutdown();
    }

}
