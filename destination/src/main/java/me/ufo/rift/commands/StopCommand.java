package me.ufo.rift.commands;

import java.util.Iterator;
import me.ufo.rift.Rift;
import me.ufo.rift.redis.Riftbound;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class StopCommand implements CommandExecutor {

  private final Rift plugin;

  public StopCommand(final Rift plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label,
                           final String[] args) {

    if (!sender.isOp()) {
      return false;
    }

    if (this.plugin.isStopping()) {
      sender.sendMessage(ChatColor.RED.toString() + "Server is already stopping.");
      return false;
    }

    sender.sendMessage(ChatColor.RED.toString() + "Stopping server...");
    this.plugin.setStopping(true);

    if (!this.plugin.getServer().getOnlinePlayers().isEmpty()) {
      sender.sendMessage(ChatColor.RED.toString() + "Sending online players to hub...");
      for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
        Riftbound.outbound().playerHubSend(player.getUniqueId());
      }

      this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, () -> {
        if (!this.plugin.getServer().getOnlinePlayers().isEmpty()) {
          for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            Riftbound.outbound().playerHubSend(player.getUniqueId());
          }
        } else {
          this.plugin.getServer().shutdown();
        }
      }, 100L, 20L);
    } else {
      this.plugin.getServer().shutdown();
    }

    return true;
  }

}
