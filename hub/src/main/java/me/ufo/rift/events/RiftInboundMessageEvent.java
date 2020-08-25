package me.ufo.rift.events;

import me.ufo.rift.redis.Riftbound;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class RiftInboundMessageEvent extends Event {

  private static final HandlerList handlers = new HandlerList();

  private final String source;
  private final Riftbound.Inbound.Action action;
  private final String[] message;

  public RiftInboundMessageEvent(final String source, final Riftbound.Inbound.Action action,
                                 final String[] message) {
    super(true);
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

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

}
