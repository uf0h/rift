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
        if ("PING".equalsIgnoreCase(event.getChannel())) {
            this.plugin.info(
                "Received riftboundmessage: {source: " + event.getSource() + ", " + event.getMessage() + "}"
            );
        }
    }

}
