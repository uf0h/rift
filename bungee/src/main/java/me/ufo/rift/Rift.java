package me.ufo.rift;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import me.ufo.rift.redis.Redis;
import me.ufo.rift.servers.RiftServer;
import me.ufo.rift.tasks.QueuePositionTask;
import me.ufo.rift.tasks.QueuePushTask;
import me.ufo.rift.util.FastUUID;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public final class Rift extends Plugin {

    private static Rift instance;

    private final RiftConfig config;
    private Redis redis;
    private ScheduledTask queuePushTask;
    private ScheduledTask queuePositionTask;

    private boolean debug;

    public Rift() throws IOException {
        this.config = new RiftConfig(this);
    }

    @Override
    public void onLoad() {
        this.getLogger().info(
            "\n       _  __ _   \n" +
                "      (_)/ _| |  \n" +
                "  _ __ _| |_| |_ \n" +
                " | '__| |  _| __|    BUNGEE SERVER\n" +
                " | |  | | | | |_ \n" +
                " |_|  |_|_|  \\__|\n"
        );

        this.redis = new Redis(this);
    }

    @Override
    public void onEnable() {
        instance = this;

        // Register commands & listeners
        final PluginManager pm = this.getProxy().getPluginManager();
        pm.registerCommand(this, new RiftCommand(this));
        pm.registerListener(this, new RiftboundMessageListener(this));

        // Register tasks
        this.queuePushTask = this.getProxy().getScheduler().schedule(
            this, new QueuePushTask(this), 1, 1, TimeUnit.SECONDS);

        this.queuePositionTask = this.getProxy().getScheduler().schedule(
            this, new QueuePositionTask(this), 1, 5, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
        this.queuePositionTask.cancel();

        for (final RiftServer server : RiftServer.getServers()) {
            if (server.hasQueue()) {
                final RiftQueue queue = server.getQueue();

                for (final QueuePlayer queuePlayer : queue.getPriorityQueue()) {
                    this.redis.async(queuePlayer.getCurrent(), "QUEUE_LEAVE", FastUUID.toString(queuePlayer.getUuid()));
                }
            }
        }

        this.queuePushTask.cancel();
        this.redis.close();
    }

    public void info(final String message) {
        this.getLogger().info(message);
    }

    public void severe(final String message) {
        this.getLogger().severe(message);
    }

    public static Rift instance() {
        return instance;
    }

    public RiftConfig config() {
        return this.config;
    }

    public Redis redis() {
        return this.redis;
    }

    public boolean debug() {
        return this.debug;
    }

    public boolean toggleDebug() {
        this.debug = !this.debug;
        return this.debug;
    }

}