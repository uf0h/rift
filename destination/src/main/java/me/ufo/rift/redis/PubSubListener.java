package me.ufo.rift.redis;

import java.util.Arrays;
import java.util.regex.Pattern;
import com.google.common.base.Preconditions;
import io.lettuce.core.pubsub.RedisPubSubListener;
import me.ufo.rift.Rift;

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

        if (messageParts.length >= 3) {
            final String source = Preconditions.checkNotNull(messageParts[0], "Source null");
            final String action = Preconditions.checkNotNull(messageParts[1], "Action null");
            final String[] message = Preconditions.checkNotNull(Arrays.copyOfRange(messageParts, 2, messageParts.length), "Message null");

            this.plugin.debug(
                "Received message: {source: " + source +
                    ", action: " + action + ", message: " + Arrays.toString(message) + "}"
            );

            final RiftboundMessageEvent event = new RiftboundMessageEvent(source, action, message);
            this.plugin.getServer().getScheduler().runTask(this.plugin,
                () -> this.plugin.getServer().getPluginManager().callEvent(event)
            );
        } else {
            this.plugin.severe("Received badly formatted message: {" + rawMessage + "}");
        }
    }

    @Override
    public void subscribed(final String channel, final long count) {
        this.plugin.debug("Listener subscribed to channel: " + channel);
    }

    @Override
    public void unsubscribed(final String channel, final long count) {
        this.plugin.debug("Listener unsubscribed from channel: " +  channel);
    }

    @Override
    public void message(final String pattern, final String channel, final String message) {}

    @Override
    public void psubscribed(final String pattern, final long count) {}

    @Override
    public void punsubscribed(final String pattern, final long count) {}

}
