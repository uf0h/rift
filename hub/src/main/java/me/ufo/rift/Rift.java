package me.ufo.rift;

import me.ufo.rift.redis.Redis;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Rift extends JavaPlugin {

    // Server name
    private final String name = "hub-1";

    // Debug
    private boolean debug;

    private Redis redis;

    @Override
    public void onLoad() {
        this.redis = new Redis(this);
    }

    @Override
    public void onEnable() {
        // Register commands
        this.getCommand("rift").setExecutor(new RiftCommand(this));

        // Register event listeners
        final PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new RiftboundMessageListener(this), this);
    }

    @Override
    public void onDisable() {
        this.redis.close();
    }

    public void debug(final String message) {
        if (this.debug) {
            this.getLogger().info(message);
        }
    }

    public void info(final String message) {
        this.getLogger().info(message);
    }

    public void severe(final String message) {
        this.getLogger().severe(message);
    }

    public boolean toggleDebug() {
        this.debug = !this.debug;
        return this.debug;
    }

    public String name() {
        return this.name;
    }

    public Redis redis() {
        return this.redis;
    }

}
