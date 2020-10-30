package me.ufo.rift.listeners;

import java.util.Arrays;
import me.ufo.rift.Rift;
import me.ufo.rift.events.RiftInboundMessageEvent;
import me.ufo.rift.redis.Riftbound;
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
      if ("HUB".equalsIgnoreCase(event.getMessage()[0])) {
        if (!this.plugin.getHubs().contains(event.getSource())) {
          this.plugin.getHubs().add(event.getSource());
        }
      }
    }
  }

}
