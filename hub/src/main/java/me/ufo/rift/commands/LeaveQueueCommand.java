package me.ufo.rift.commands;

import me.ufo.rift.Rift;
import me.ufo.rift.obj.QueuePlayer;
import me.ufo.rift.redis.Riftbound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class LeaveQueueCommand implements CommandExecutor {

  private final Rift plugin;

  public LeaveQueueCommand(final Rift plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label,
                           final String[] args) {

    if (!(sender instanceof Player)) {
      sender.sendMessage("Player only command.");
      return false;
    }

    final QueuePlayer queuePlayer = QueuePlayer.fromUUID(((Player) sender).getUniqueId());
    if (queuePlayer != null) {
      Riftbound.outbound().playerQueueLeave(queuePlayer.getUuid(), queuePlayer.getDestination());
      QueuePlayer.getPlayers().remove(queuePlayer);
    } else {
      sender.sendMessage("You are not in a queue.");
    }
    return true;
  }

}
