package me.ufo.rift.commands;

import java.util.Arrays;
import me.ufo.rift.Rift;
import me.ufo.rift.queues.QueuePlayer;
import me.ufo.rift.queues.RiftQueue;
import me.ufo.rift.server.RiftServer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

public final class RiftCommand extends Command {

  private final Rift plugin;

  public RiftCommand(final Rift plugin) {
    super("brift", "rift.command", "br");
    this.plugin = plugin;
  }

  @Override
  public void execute(final CommandSender sender, final String[] args) {
    if (!sender.hasPermission("rift.command")) {
      return;
    }

    if (args.length <= 0) {
      return;
    }

    final ComponentBuilder out = new ComponentBuilder();

    switch (args[0].toLowerCase()) {
      case "displayname": {
        if (args.length != 2) {
          sender.sendMessage("Usage: /br displayname <queue>");
          return;
        }

        final RiftQueue queue = RiftQueue.fromName(args[1]);
        if (queue == null) {
          sender.sendMessage("That is not a valid queue.");
          return;
        }

        sender.sendMessage(args[1] + "'s display name is set to: " + queue.getDisplayName());
        return;
      }

      case "setdisplayname" : {
        if (args.length < 3) {
          sender.sendMessage("Usage: /br setdisplayname <queue> <name>");
          return;
        }

        final RiftQueue queue = RiftQueue.fromName(args[1]);
        if (queue == null) {
          sender.sendMessage("That is not a valid queue.");
          return;
        }

        final String displayName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        queue.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        this.plugin.config().saveQueue(queue);
        break;
      }

      case "debugmode":
        final boolean mode = this.plugin.toggleDebug();
        sender.sendMessage(mode ? "Debug mode is enabled." : "Debug mode is disabled.");
        break;

      case "debug":
        sender.sendMessage("RiftServers: " + RiftServer.getServers().size());
        for (final RiftServer server : RiftServer.getServers()) {
          sender.sendMessage(server.toString());
        }
        sender.sendMessage("RiftQueues: " + RiftQueue.getQueues().size());
        for (final RiftQueue queue : RiftQueue.getQueues()) {
          sender.sendMessage(queue.toString());
        }
        sender.sendMessage("QueuePlayers: " + QueuePlayer.getPlayers().size());
        for (final QueuePlayer player : QueuePlayer.getPlayers()) {
          sender.sendMessage(player.toString());
        }
        break;

      case "test":
        if (args.length != 5) {
          sender.sendMessage("Usage: /rift test <-a:-s> <destination> <action> <message>");
          return;
        }

        if ("-a".equalsIgnoreCase(args[1])) {
          this.plugin.redis().async(args[2], args[3], args[4]);
        } else if ("-s".equalsIgnoreCase(args[1])) {
          this.plugin.redis().sync(args[2], args[3], args[4]);
        } else {
          sender.sendMessage("Usage: /rift test <-a:-s> <destination> <action> <message>");
          return;
        }
        break;

      case "servers":
        if (RiftServer.getServers().isEmpty()) {
          out.color(ChatColor.RED).append("No rift servers detected.");
        } else {
          out.append("Servers:");
          for (final RiftServer server : RiftServer.getServers()) {
            out.color(ChatColor.AQUA).append(server.getName()).append("\n")
              .color(ChatColor.WHITE)
              .append("  ").append(server.getServerType().name()).append("\n")
              .append("  ").append(String.valueOf(server.getOnlinePlayers())).append("\n")
              .append("  ").append(server.isOnline() ? "Online" : "Offline").append("\n")
              .append("  ").append(server.hasQueue() ? "Has queue" : "No queue").append("\n");
          }
        }

        sender.sendMessage(out.create());
        break;

      case "queues":
        if (RiftQueue.getQueues().isEmpty()) {
          out.color(ChatColor.RED).append("No rift queues detected.");
        } else {
          out.append("Queues:");
          for (final RiftQueue queue : RiftQueue.getQueues()) {
            out.color(ChatColor.AQUA).append(queue.getName()).append("\n")
              .color(ChatColor.WHITE)
              .append("  Queuing: ").append(queue.isQueuing() ? "true" : "false").append("\n");
          }
        }

        sender.sendMessage(out.create());
        break;

      case "togglequeue":
        if (args.length != 2) {
          sender.sendMessage("Usage: /rift pausequeue <destination>");
          return;
        }

        final RiftServer riftServer = RiftServer.fromName(args[1]);
        if (riftServer == null) {
          sender.sendMessage("Destination server not recognized.");
          return;
        }

        if (riftServer.isHubServer()) {
          sender.sendMessage("There are no queues for hub servers.");
          return;
        }

        if (!riftServer.hasQueue()) {
          sender.sendMessage("Destination server {" + args[1] + "} does not have a queue.");
          return;
        }

        final boolean queuing = !riftServer.getQueue().isQueuing();
        riftServer.getQueue().setQueuing(queuing);

        sender.sendMessage("Destination servers' queue {" + args[1] + "} has been " +
                           (queuing ? "enabled" : "disabled") + "."
        );

        this.plugin.config().saveQueue(riftServer.getQueue());
        break;

      case "setqueue":
        if (args.length != 2) {
          sender.sendMessage("Usage: /rift setqueue <destination>");
          return;
        }

        // TODO: better commands
        // changed from riftServer due to above duplicate variable
        final RiftServer server = RiftServer.fromName(args[1]);
        if (server == null) {
          sender.sendMessage("Destination server not recognized.");
          return;
        }

        if (server.isHubServer()) {
          sender.sendMessage("You cannot assign a queue to a hub server.");
          return;
        }

        if (!server.isOnline()) {
          sender.sendMessage("Destination server {" + args[1] + "} is not online.");
          return;
        }

        if (server.hasQueue()) {
          sender.sendMessage("Destination server {" + args[1] + "} already has a queue.");
          return;
        }

        final RiftQueue queue = server.attachQueue(args[1]);
        sender.sendMessage("Queue set for destination server {" + args[1] + "}.");
        this.plugin.config().saveQueue(queue);
        break;

      default:
        break;
    }
  }

}
