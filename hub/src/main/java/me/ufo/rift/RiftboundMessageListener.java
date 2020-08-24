package me.ufo.rift;

import java.util.Arrays;
import java.util.Queue;
import java.util.UUID;
import me.ufo.rift.redis.RiftboundMessageEvent;
import me.ufo.rift.util.FastUUID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class RiftboundMessageListener implements Listener {

    private final Rift plugin;

    public RiftboundMessageListener(final Rift plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRiftMessageReceive(final RiftboundMessageEvent event) {
        if (event.getSource().equalsIgnoreCase(this.plugin.name())) {
            if (this.plugin.debug()) {
                this.plugin.info(
                    "Received riftboundmessage from this server: {source: " + event.getSource() +
                        ", action: " + event.getAction() +
                        ", message: " + Arrays.toString(event.getMessage()) + "}"
                );
            }
            return;
        }

        this.plugin.info(
            "Received riftboundmessage: {source: " + event.getSource() +
                ", action: " + event.getAction() +
                ", message: " + Arrays.toString(event.getMessage()) + "}"
        );

        if ("PLAYER_INFO_RESPONSE".equalsIgnoreCase(event.getAction())) {
            final UUID uuid = FastUUID.fromString(event.getMessage()[0]);

            QueuePlayer queuePlayer = QueuePlayer.get(uuid);
            if (queuePlayer == null) {
                queuePlayer = new QueuePlayer(uuid, event.getSource());
            }

            queuePlayer.setRank(event.getMessage()[1]);
            queuePlayer.setPriority(Integer.parseInt(event.getMessage()[2]));

            this.plugin.redis().async(
                "bungee",
                "QUEUE_JOIN",
                queuePlayer.getDestination() + "," + FastUUID.toString(queuePlayer.getUuid()) +
                    "," + queuePlayer.getRank() + "," + queuePlayer.getPriority()
            );
        }
        // ... when player is getting sent to destination server
        else if ("QUEUE_SEND".equalsIgnoreCase(event.getAction())) {
            this.plugin.sendPlayerToServer(
                this.plugin.getServer().getPlayer(FastUUID.fromString(event.getMessage()[0])),
                event.getMessage()[1]
            );
        }
        // ... when bungee drops, kick all queueplayers off queue
        else if ("QUEUE_LEAVE".equalsIgnoreCase(event.getAction())) {
            final UUID uuid = FastUUID.fromString(event.getMessage()[0]);
            final QueuePlayer queuePlayer = QueuePlayer.get(uuid);

            QueuePlayer.getPlayers().remove(queuePlayer);
        }

        // TODO: have list of players that have joined a queue to check dupe joining
    }

}
