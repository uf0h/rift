package me.ufo.rift.tasks;

import me.ufo.rift.Rift;

public final class PingTask implements Runnable {

    private final Rift plugin;

    public PingTask(final Rift plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        final String out =
            this.plugin.getServer().getOnlinePlayers().size() + "," +
                (this.plugin.getServer().hasWhitelist() ? "WHITELISTED" : "ONLINE");

        this.plugin.redis().async("all", "", out);
    }

}
