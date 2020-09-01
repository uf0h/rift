package me.ufo.rift.listeners;

import java.util.Arrays;
import me.ufo.rift.Rift;
import me.ufo.rift.events.RiftInboundMessageEvent;
import me.ufo.rift.redis.Riftbound;
import me.ufo.rift.util.FastUUID;
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
        ", message: " + Arrays.toString(event.getMessage()) + "}"
      );
    }

    if (event.getAction() == Riftbound.Inbound.Action.PLAYER_INFO_REQUEST) {
      this.plugin.provider().send(event.getSource(), FastUUID.fromString(event.getMessage()[0]));
    }
  }

}
