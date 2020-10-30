package me.ufo.rift.commands;

import me.ufo.rift.Rift;
import me.ufo.rift.obj.QueuePlayer;
import me.ufo.rift.permission.PriorityProvider;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class JoinQueueCommand implements CommandExecutor {

  private final Rift plugin;

  public JoinQueueCommand(final Rift plugin) {
    this.plugin = plugin;
  }

  // NOTE: if you change hub, the new hub wont have the queueplayer which will allow them to enter the queue
  // ... again
  // TODO: PLAYER_QUEUE_JOIN - [uuid, destination] send to other hubs
  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label,
                           final String[] args) {

    if (!(sender instanceof Player)) {
      sender.sendMessage("Player only command.");
      return false;
    }

    if (args.length != 1) {
      sender.sendMessage(ChatColor.RED.toString() + "Usage: /joinqueue <queue>");
      return false;
    }

    if (QueuePlayer.fromUUID(((Player) sender).getUniqueId()) != null) {
      sender.sendMessage(new String[] {
        ChatColor.RED.toString() + "You are already in a queue.",
        ChatColor.RED.toString() + "To leave the queue do: /leavequeue"
      });
      return false;
    }

    PriorityProvider.get().check(((Player) sender).getUniqueId(), args[0]);
    return true;
  }

}
