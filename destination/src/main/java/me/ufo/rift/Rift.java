package me.ufo.rift;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.ufo.rift.commands.HubCommand;
import me.ufo.rift.commands.RiftCommand;
import me.ufo.rift.commands.StopCommand;
import me.ufo.rift.listeners.RiftInboundListener;
import me.ufo.rift.listeners.StopListener;
import me.ufo.rift.obj.RiftServerStatus;
import me.ufo.rift.permission.PriorityProvider;
import me.ufo.rift.redis.Redis;
import me.ufo.rift.redis.Riftbound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public final class Rift extends JavaPlugin {

  private static Rift instance;

  private final String name;
  private Redis redis;
  private BukkitTask pingTask;
  private PriorityProvider provider;
  private boolean registered;
  private boolean debug;

  private boolean stopping;
  private List<String> hubs;

  public Rift() {
    instance = this;
    this.saveDefaultConfig();
    this.name = this.getConfig().getString("server-name");
  }

  @Override
  public void onLoad() {
    this.getLogger().info(
      "\n     _  __ _   \n" +
      "      (_)/ _| |  \n" +
      "  _ __ _| |_| |_ \n" +
      " | '__| |  _| __|    DESTINATION SERVER: " + this.name + "\n" +
      " | |  | | | | |_ \n" +
      " |_|  |_|_|  \\__|\n"
    );

    this.redis = new Redis(this);

    this.provider = PriorityProvider.setup(this);
    if (this.provider == null) {
      this.severe("No permissions plugin has been detected.");
      this.getServer().getPluginManager().disablePlugin(this);
    }

    this.hubs = new ArrayList<>(3);
  }

  @Override
  public void onEnable() {
    // Register commands
    this.getCommand("rift").setExecutor(new RiftCommand(this));
    this.getCommand("hub").setExecutor(new HubCommand(this));
    this.getCommand("stop").setExecutor(new StopCommand(this));

    // Register event listeners
    final PluginManager pm = this.getServer().getPluginManager();
    pm.registerEvents(new RiftInboundListener(this), this);
    pm.registerEvents(new StopListener(this), this);

    // Register repeating tasks
    final BukkitScheduler scheduler = this.getServer().getScheduler();
    this.pingTask = scheduler.runTaskTimerAsynchronously(this, () -> Riftbound.outbound().ping(), 60L, 60L);
  }

  @Override
  public void onDisable() {
    this.pingTask.cancel();
    this.redis.close();
  }

  public void sendAllToHubs() {
    final List<String> hubs = this.hubs;
    final int hubsAvailable = hubs.size();

    if (hubsAvailable == 0) {
      for (final Player player : this.getServer().getOnlinePlayers()) {
        Riftbound.outbound().playerHubSend(player.getUniqueId(), false);
      }
      return;
    }

    final Iterator<? extends Player> iterator = this.getServer().getOnlinePlayers().iterator();

    int current = 0;
    while(iterator.hasNext()) {
      current = current % hubsAvailable;
      final Player player = iterator.next();

      Riftbound.outbound().playerHubSend(player.getUniqueId(), hubs.get(current), false);

      current++;
    }
  }

  public void info(final String message) {
    this.getLogger().info(message);
  }

  public void severe(final String message) {
    this.getLogger().severe(message);
  }

  public String response() {
    return "DESTINATION" + "," + this.getServer().getOnlinePlayers().size() + "," +
           (this.getServer().hasWhitelist() ?
            RiftServerStatus.WHITELISTED.name() : RiftServerStatus.ONLINE.name());
  }

  public String name() {
    return this.name;
  }

  public Redis redis() {
    return this.redis;
  }

  public PriorityProvider provider() {
    return this.provider;
  }

  public boolean registered() {
    return this.registered;
  }

  public boolean toggleRegistered() {
    this.registered = !this.registered;
    return this.registered;
  }

  public boolean debug() {
    return this.debug;
  }

  public boolean toggleDebug() {
    this.debug = !this.debug;
    return this.debug;
  }

  public boolean isStopping() {
    return this.stopping;
  }

  public void setStopping(final boolean stopping) {
    this.stopping = stopping;
  }

  public List<String> getHubs() {
    return hubs;
  }

  public static Rift instance() {
    return instance;
  }

}
