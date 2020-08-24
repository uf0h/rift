package me.ufo.rift;

import me.ufo.rift.util.FastUUID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerListener implements Listener {

    private final Rift plugin;

    public PlayerListener(final Rift plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuitEvent(final PlayerQuitEvent event) {
        this.plugin.info(event.getPlayer().getName() + " is quitting");
        final QueuePlayer queuePlayer = QueuePlayer.get(event.getPlayer().getUniqueId());
        if (queuePlayer != null) {
            this.plugin.redis().async(
                "bungee",
                "QUEUE_LEAVE",
                queuePlayer.getDestination() + "," + FastUUID.toString(queuePlayer.getUuid())
            );

            QueuePlayer.getPlayers().remove(queuePlayer);
        }
    }

}
