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
    public void onRiftMessageRecieve(final RiftboundMessageEvent event) {
        if (event.getChannel().equalsIgnoreCase("PING")) {
            this.plugin.info(
                "Received riftboundmessage: {source: " + event.getSource() + ", " + event.getMessage() + "}"
            );
        }
    }

}
