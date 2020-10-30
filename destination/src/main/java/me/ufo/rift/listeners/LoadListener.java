package me.ufo.rift.listeners;

import me.ufo.rift.Rift;
import me.ufo.rift.util.Style;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public final class LoadListener implements Listener {

  private Rift plugin;
  private int loadRunnableId;

  public LoadListener(final Rift plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onPluginEnableEvent(final PluginEnableEvent event) {
    Bukkit.getScheduler().cancelTask(loadRunnableId);
    loadRunnableId = getLoadedTask().getTaskId();
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onAsyncPlayerPreLoginEvent(final AsyncPlayerPreLoginEvent event) {
    if (!plugin.isLoaded()) {
      event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Style.translate("&aServer loading. Try again later."));
    }
  }

  private BukkitTask getLoadedTask() {
    return new BukkitRunnable() {
      @Override
      public void run() {
        Rift.instance().setLoaded(true);
        Rift.instance().enablePingTask();
        Rift.instance().info("=== ALL PLUGINS LOADED. ACCEPTING INCOMING PLAYERS ===");
      }
    }.runTaskLater(Rift.instance(), 100L);
  }

}

