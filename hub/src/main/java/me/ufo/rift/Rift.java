package me.ufo.rift;

import me.ufo.rift.commands.JoinQueueCommand;
import me.ufo.rift.commands.LeaveQueueCommand;
import me.ufo.rift.commands.RiftCommand;
import me.ufo.rift.listeners.RiftInboundListener;
import me.ufo.rift.obj.RiftServerStatus;
import me.ufo.rift.redis.Redis;
import me.ufo.rift.redis.Riftbound;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public final class Rift extends JavaPlugin {

  private static Rift instance;

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
      "\n\n" +
      "       _  __ _  \n" +
      "      (_)/ _| | \n" +
      "  _ __ _| |_| |_\n" +
      " | '__| |  _| __|    HUB SERVER: " + this.name + "\n" +
      " | |  | | | | |_\n" +
      " |_|  |_|_|  \\__|\n"
    );

    this.redis = new Redis(this);
  }

  @Override
  public void onEnable() {
    instance = this;

    // Register commands
    this.getCommand("rift").setExecutor(new RiftCommand(this));
    this.getCommand("joinqueue").setExecutor(new JoinQueueCommand(this));
    this.getCommand("leavequeue").setExecutor(new LeaveQueueCommand(this));

    // Register event listeners
    final PluginManager pm = this.getServer().getPluginManager();
    pm.registerEvents(new RiftInboundListener(this), this);

    this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

    // Register repeating tasks
    final BukkitScheduler scheduler = this.getServer().getScheduler();
    this.pingTask = scheduler.runTaskTimerAsynchronously(this, () -> Riftbound.outbound().ping(), 60L, 60L);
  }

  @Override
  public void onDisable() {
    this.pingTask.cancel();
    this.redis.close();
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

  public static Rift instance() {
    return instance;
  }

}
