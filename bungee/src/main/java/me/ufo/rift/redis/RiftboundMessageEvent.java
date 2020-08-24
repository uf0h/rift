package me.ufo.rift.redis;

import net.md_5.bungee.api.plugin.Event;

public final class RiftboundMessageEvent extends Event {

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

}
