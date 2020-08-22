package me.ufo.rift;

import me.ufo.rift.redis.RiftboundMessageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class RiftboundMessageListener implements Listener {

    private final Rift plugin;

    public RiftboundMessageListener(final Rift plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRiftMessageReceive(final RiftboundMessageEvent event) {
        this.plugin.debug(
            "Received riftboundmessage: {source: " + event.getSource() +
                ", channel: " + event.getChannel() +
                ", message: " + event.getMessage() + "}"
        );
    }

}
