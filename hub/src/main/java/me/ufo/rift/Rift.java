package me.ufo.rift;

import me.ufo.rift.redis.Redis;
import me.ufo.rift.tasks.PingTask;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Rift extends JavaPlugin {

    private final String name;
    private boolean debug;

    private Redis redis;

    public Rift() {
        this.saveDefaultConfig();
        this.name = this.getConfig().getString("server-name");
    }

    @Override
    public void onLoad() {
        this.getLogger().info(
            "\n       _  __ _   \n" +
            "      (_)/ _| |  \n" +
            "  _ __ _| |_| |_ \n" +
            " | '__| |  _| __|    HUB SERVER: " + this.name + "\n" +
            " | |  | | | | |_ \n" +
            " |_|  |_|_|  \\__|\n"
        );

        this.redis = new Redis(this);
    }

    @Override
    public void onEnable() {
        // Register commands
        this.getCommand("rift").setExecutor(new RiftCommand(this));

        // Register event listeners
        final PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new RiftboundMessageListener(this), this);

        // Register tasks
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new PingTask(this), 20L, 60L);
    }

    @Override
    public void onDisable() {
        this.redis.close();
    }

    public void info(final String message) {
        this.getLogger().info(message);
    }

    public void severe(final String message) {
        this.getLogger().severe(message);
    }

    public String name() {
        return this.name;
    }

    public boolean debug() {
        return this.debug;
    }

    public boolean toggleDebug() {
        this.debug = !this.debug;
        return this.debug;
    }

    public Redis redis() {
        return this.redis;
    }

}
