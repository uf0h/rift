package me.ufo.rift.events;

import me.ufo.rift.redis.Riftbound;
import net.md_5.bungee.api.plugin.Event;

public final class RiftInboundMessageEvent extends Event {

  private final String source;
  private final Riftbound.Inbound.Action action;
  private final String[] message;

  public RiftInboundMessageEvent(final String source, final Riftbound.Inbound.Action action,
                                 final String[] message) {
    this.source = source;
    this.action = action;
    this.message = message;
  }

  public String getSource() {
    return this.source;
  }

  public Riftbound.Inbound.Action getAction() {
    return this.action;
  }

  public String[] getMessage() {
    return this.message;
  }

}
