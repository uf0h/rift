package me.ufo.rift.redis;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class RiftboundMessageEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final String source;
    private final String action;
    private final String[] message;

    public RiftboundMessageEvent(final String source, final String action, final String[] message) {
        this.source = source;
        this.action = action;
        this.message = message;
    }

    public String getSource() {
        return this.source;
    }

    public String getAction() {
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
