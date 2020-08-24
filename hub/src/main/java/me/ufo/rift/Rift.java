package me.ufo.rift;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import me.ufo.rift.redis.Redis;
import me.ufo.rift.tasks.PingTask;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class Rift extends JavaPlugin {

    private final String name;
    private Redis redis;
    private BukkitTask pingTask;
    private boolean debug;

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
        pm.registerEvents(new PlayerListener(this), this);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // Register repeating tasks
        this.pingTask = this.getServer().getScheduler().runTaskTimerAsynchronously(
            this, new PingTask(this), 60L, 60L);

        this.redis.async("all", "ONLINE", "HUB");
    }

    @Override
    public void onDisable() {
        this.pingTask.cancel();
        this.redis().async("all", "OFFLINE", "").thenAccept(ignored -> this.redis.close());
    }

    public void info(final String message) {
        this.getLogger().info(message);
    }

    public void severe(final String message) {
        this.getLogger().severe(message);
    }

    public String response() {
        return "HUB" + "," + this.getServer().getOnlinePlayers().size() + "," +
            (this.getServer().hasWhitelist() ?
             RiftServerStatus.WHITELISTED.name() : RiftServerStatus.ONLINE.name());
    }

    public void sendPlayerToServer(final Player player, final String server) {
        this.info("sending player to " + server);
        try (final ByteArrayOutputStream b = new ByteArrayOutputStream();
             final DataOutputStream out = new DataOutputStream(b)) {

            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(this, "BungeeCord", b.toByteArray());
        } catch (final IOException ignored) {
            player.sendMessage("Failed to send you to: " + server);
        }
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
