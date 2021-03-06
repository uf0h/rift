package me.ufo.rift.listeners;

import me.ufo.rift.Rift;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class StopListener implements Listener {

  private final Rift plugin;

  public StopListener(final Rift plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerJoin(final AsyncPlayerPreLoginEvent event) {
    if (this.plugin.isStopping()) {
      event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
      event.setKickMessage(ChatColor.RED.toString() + "This server is restarting.");
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onBlockPlace(final BlockPlaceEvent event) {
    if (this.plugin.isStopping()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onBlockBreak(final BlockBreakEvent event) {
    if (this.plugin.isStopping()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
    if (this.plugin.isStopping()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {
    if (this.plugin.isStopping()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerMove(final PlayerMoveEvent event) {
    if (this.plugin.isStopping()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerTeleport(final PlayerTeleportEvent event) {
    if (this.plugin.isStopping()) {
      event.setCancelled(true);
    }
  }

}
