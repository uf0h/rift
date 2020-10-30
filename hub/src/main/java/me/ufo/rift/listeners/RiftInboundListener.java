package me.ufo.rift.listeners;

import java.util.Arrays;
import java.util.UUID;
import me.ufo.rift.Rift;
import me.ufo.rift.events.RiftInboundMessageEvent;
import me.ufo.rift.obj.QueuePlayer;
import me.ufo.rift.redis.Riftbound;
import me.ufo.rift.util.FastUUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public final class RiftInboundListener implements Listener {

  private final Rift plugin;

  public RiftInboundListener(final Rift plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onRiftMessageReceive(final RiftInboundMessageEvent event) {
    if (this.plugin.debug()) {
      this.plugin.info(
        "Received riftboundmessage: {source: " + event.getSource() +
        ", action: " + event.getAction() +
        ", message: " + (event.getMessage() == null ? "null" : Arrays.toString(event.getMessage())) + "}"
      );
    }

    if (event.getAction() == Riftbound.Inbound.Action.PING) {
      return;
    }

    final UUID uuid = FastUUID.fromString(event.getMessage()[0]);

    switch (event.getAction()) {
      case PLAYER_CHANGE_SERVER:
      case PLAYER_PROXY_DISCONNECT:
        QueuePlayer.getPlayers().remove(QueuePlayer.fromUUID(uuid));
        break;

      /*case PLAYER_INFO_RESPONSE: {
        QueuePlayer player = QueuePlayer.fromUUID(uuid);
        if (player == null) {
          player = new QueuePlayer(uuid, event.getSource());
        }

        player.setRank(event.getMessage()[1]);
        player.setPriority(Integer.parseInt(event.getMessage()[2]));

        final Player bukkitPlayer = this.plugin.getServer().getPlayer(uuid);

        bukkitPlayer.sendMessage(ChatColor.RED.toString() + "Attempting to send " + bukkitPlayer.getName() + " to " + player.getDestination() + ".");

        Riftbound.outbound()
          .playerQueueJoin(uuid, player.getDestination(), player.getRank(), player.getPriority());
        break;
      }*/

      case SERVER_RESTARTING:

        break;

      default:
        break;
    }
  }

}
