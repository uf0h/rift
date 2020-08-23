package me.ufo.rift;

import java.util.Arrays;
import java.util.UUID;
import me.ufo.rift.redis.RiftboundMessageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class RiftboundMessageListener implements Listener {

    private final Rift plugin;

    public RiftboundMessageListener(final Rift plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRiftMessageReceive(final RiftboundMessageEvent event) {
        if (event.getSource().equalsIgnoreCase("rift:" + this.plugin.name())) {
            this.plugin.debug(
                "Received riftboundmessage from this server: {source: " + event.getSource() +
                    ", action: " + event.getAction() +
                    ", message: " + Arrays.toString(event.getMessage()) + "}"
            );
            return;
        }

        this.plugin.info(
            "Received riftboundmessage: {source: " + event.getSource() +
                ", action: " + event.getAction() +
                ", message: " + Arrays.toString(event.getMessage()) + "}"
        );

        if ("PLAYER_INFO_REQUEST".equalsIgnoreCase(event.getAction())) {
            this.plugin.provider().send(event.getSource(), UUID.fromString(event.getMessage()[0]));
        }
    }

}
