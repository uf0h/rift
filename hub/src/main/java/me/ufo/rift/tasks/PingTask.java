package me.ufo.rift.tasks;

import me.ufo.rift.Rift;

public final class PingTask implements Runnable {

    private final Rift plugin;

    public PingTask(final Rift plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        final String out = this.plugin.response();

        this.plugin.redis().async("all", "PING", out);
    }

}
