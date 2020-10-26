package me.ufo.rift.redis;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import io.lettuce.core.pubsub.RedisPubSubListener;
import me.ufo.rift.Rift;
import me.ufo.rift.events.RiftInboundMessageEvent;
import me.ufo.rift.redis.Riftbound.Inbound.Action;

import static com.google.common.base.Preconditions.checkNotNull;

public final class PubSubListener implements RedisPubSubListener<String, String> {

  private final Rift plugin;
  private final Pattern comma;

  public PubSubListener(final Rift plugin) {
    this.plugin = plugin;
    this.comma = Pattern.compile(",");
  }

  @Override
  public void message(final String listenerChannel, final String rawMessage) {
    final String[] messageParts = this.comma.split(rawMessage);

    if (messageParts.length == 2) {
      final String source = checkNotNull(messageParts[0], "Source null");
      if ("BUNGEE".equalsIgnoreCase(source)) {
        return;
      }

      final Action action = Action.get(messageParts[1]);
      if (action == null) {
        return;
      }

      final RiftInboundMessageEvent event = new RiftInboundMessageEvent(source, action, null);
      this.plugin.getProxy().getScheduler().schedule(
        this.plugin, () -> this.plugin.getProxy().getPluginManager().callEvent(event), 0L, TimeUnit.SECONDS);
    } else if (messageParts.length >= 3) {
      final String source = checkNotNull(messageParts[0], "Source null");
      if ("BUNGEE".equalsIgnoreCase(source)) {
        return;
      }

      final Action action = Action.get(messageParts[1]);
      if (action == null) {
        return;
      }

      final String[] message = checkNotNull(
        Arrays.copyOfRange(messageParts, 2, messageParts.length), "Message null");

      final RiftInboundMessageEvent event = new RiftInboundMessageEvent(source, action, message);
      this.plugin.getProxy().getScheduler().schedule(
        this.plugin, () -> this.plugin.getProxy().getPluginManager().callEvent(event), 0L, TimeUnit.SECONDS);
    } else {
      this.plugin.severe("Received badly formatted message: {" + rawMessage + "}");
    }
  }

  @Override
  public void subscribed(final String channel, final long count) {
    if (this.plugin.debug()) {
      this.plugin.info("Listener subscribed to channel: " + channel);
    }
  }

  @Override
  public void unsubscribed(final String channel, final long count) {
    if (this.plugin.debug()) {
      this.plugin.info("Listener unsubscribed from channel: " + channel);
    }
  }

  @Override
  public void message(final String pattern, final String channel, final String message) {
  }

  @Override
  public void psubscribed(final String pattern, final long count) {
  }

  @Override
  public void punsubscribed(final String pattern, final long count) {
  }

}
