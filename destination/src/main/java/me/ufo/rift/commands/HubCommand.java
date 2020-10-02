package me.ufo.rift.commands;

import me.ufo.rift.Rift;
import me.ufo.rift.redis.Riftbound;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class HubCommand implements CommandExecutor {

  private final Rift plugin;

  public HubCommand(final Rift plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label,
                           final String[] args) {

    if (!(sender instanceof Player)) {
      sender.sendMessage("Player only command.");
      return false;
    }

    final Player player = (Player) sender;

    player.sendMessage(ChatColor.RED.toString() + "Attempting to send " + player.getName() + " to a hub.");
    Riftbound.outbound().playerHubSend(player.getUniqueId(), true);
    return true;
  }

}
