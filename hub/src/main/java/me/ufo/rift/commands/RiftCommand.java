package me.ufo.rift.commands;

import me.ufo.rift.Rift;
import me.ufo.rift.obj.QueuePlayer;
import me.ufo.rift.redis.Riftbound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class RiftCommand implements CommandExecutor {

  private final Rift plugin;

  public RiftCommand(final Rift plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command cmd, final String l,
                           final String[] args) {

    if (!sender.isOp() && !sender.hasPermission("rift.command")) {
      return false;
    }

    if (args.length <= 0) {
      return false;
    }
    // TODO: add /lag command (mem usage)
    switch (args[0].toLowerCase()) {
      case "queueplayers":
        sender.sendMessage("" + QueuePlayer.getPlayers().size());
        break;

      case "debug":
        sender.sendMessage(this.plugin.toggleDebug() ? "Debug mode is enabled." : "Debug mode is disabled.");
        break;

      case "test":
        if (args.length != 5) {
          sender.sendMessage("Usage: /rift test <-a:-s> <destination> <action> <message>");
          return false;
        }

        if ("-a".equalsIgnoreCase(args[1])) {
          this.plugin.redis().async(args[2], args[3], args[4]);
        } else if ("-s".equalsIgnoreCase(args[1])) {
          this.plugin.redis().sync(args[2], args[3], args[4]);
        } else {
          sender.sendMessage("Usage: /rift test <-a:-s> <destination> <action> <message>");
          return false;
        }
        break;

      case "ping":
        Riftbound.outbound().ping();
        break;

      default:
        return false;
    }

    return true;
  }

}
