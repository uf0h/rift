package me.ufo.rift;

import me.ufo.rift.permission.PermissionProvider;
import me.ufo.rift.redis.Redis;
import me.ufo.rift.tasks.PingTask;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Rift extends JavaPlugin {

    private static Rift instance;

    private final String name;
    private boolean debug;

    private Redis redis;

    private PermissionProvider provider;

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
            " | '__| |  _| __|    DESTINATION SERVER: " + this.name + "\n" +
            " | |  | | | | |_ \n" +
            " |_|  |_|_|  \\__|\n"
        );

        this.redis = new Redis(this);

        this.provider = PermissionProvider.setup(this);
        if (this.provider == null) {
            this.severe("No permissions plugin has been detected.");
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onEnable() {
        instance = this;

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

    public static Rift instance() {
        return instance;
    }

    public String name() {
        return this.name;
    }

    public Redis redis() {
        return this.redis;
    }

    public PermissionProvider provider() {
        return this.provider;
    }

}
