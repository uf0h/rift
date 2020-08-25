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
      "rift:BUNGEE",
      "rift:ALL"
    );
  }

  public RedisFuture<Long> async(final String destination, final String action, final String message) {
    if (this.plugin.debug()) {
      this.plugin.info("Publish async: (" + destination + ", " + action + ", " + message + ")");
    }

    return this.connection.async().publish(
      // Destination
      "rift:" + destination,
      // Source
      Riftbound.Server.BUNGEE.name()
      + "," +
      // Action
      action
      + "," +
      // Message
      message
    );
  }

  public Long sync(final String destination, final String action, final String message) {
    if (this.plugin.debug()) {
      this.plugin.info("Publish sync: (" + destination + ", " + action + ", " + message + ")");
    }

    return this.connection.sync().publish(
      // Destination
      "rift:" + destination,
      // Source
      Riftbound.Server.BUNGEE.name()
      + "," +
      // Action
      action
      + "," +
      // Message
      message
    );
  }

  @Override
  public void close() {
    this.connection.close();
    this.psConnection.close();
    this.redisClient.shutdown();
  }

}
