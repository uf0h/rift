package me.ufo.rift.commands;

import java.util.Arrays;
import me.ufo.rift.Rift;
import me.ufo.rift.queues.QueuePlayer;
import me.ufo.rift.queues.RiftQueue;
import me.ufo.rift.server.RiftServer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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
    final ComponentBuilder out = new ComponentBuilder();

    if (args.length <= 0) {
      out.append("/brift").color(ChatColor.RED).underlined(true)
        .append(" command usage:").color(ChatColor.RED).underlined(false)
        .append("\n")
        .append("        toggle <queue> ").color(ChatColor.WHITE).append("Toggle the queue")
        .color(ChatColor.YELLOW)
        .append("\n")
        .append("        displayname <queue> ").color(ChatColor.WHITE).append("Show the name of a queue").color(ChatColor.YELLOW)
        .append("\n")
        .append("        setdisplayname <queue> <name> ").color(ChatColor.WHITE).append("Set the name of a queue").color(ChatColor.YELLOW)
        .append("\n\n")
        .append("        servers ").color(ChatColor.WHITE).append("List rift servers").color(ChatColor.YELLOW)
        .append("\n")
        .append("        queues ").color(ChatColor.WHITE).append("List rift queues").color(ChatColor.YELLOW);

      sender.sendMessage(out.create());
      return;
    }

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

      case "setdisplayname": {
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
          out.append("No rift servers detected.").color(ChatColor.RED);
        } else {
          out.append("Servers:").color(ChatColor.GREEN).append("\n");
          for (final RiftServer server : RiftServer.getServers()) {
            final ComponentBuilder hover = new ComponentBuilder(server.getName() + " server:")
              .color(ChatColor.YELLOW)
              .append("\n")
              .append("Type: ").color(ChatColor.AQUA)
              .append(server.getServerType().name()).color(ChatColor.WHITE)
              .append("\n")
              .append("Online: ").color(ChatColor.AQUA).append(server.getOnlinePlayers() + " players").color(ChatColor.WHITE);

            if (server.isDestinationServer()) {
              if (server.hasQueue()) {
                hover.append("\n").append("Queue: ").color(ChatColor.AQUA).append(" Set").color(ChatColor.GREEN)
                  .append("\n");

                if (server.getQueue().isQueuing()) {
                  hover.append("Queueing: ").color(ChatColor.AQUA).append("YES").color(ChatColor.GREEN)
                    .append("\n");
                } else {
                  hover.append("Queueing: ").color(ChatColor.AQUA).append("NO").color(ChatColor.RED)
                    .append("\n");
                }

                hover.append("Queued: ").color(ChatColor.AQUA).append(server.getQueue().getPriorityQueue().size() + " players")
                  .color(ChatColor.WHITE);
              } else {
                hover.append("\n").append("Queue: ").color(ChatColor.AQUA).append(" Not Set").color(ChatColor.RED);
              }
            }

            final ComponentBuilder comp = new ComponentBuilder(server.getName())
              .color(ChatColor.YELLOW)
              .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.create()));

            out.append(comp.create()).append("\n");
          }
        }

        sender.sendMessage(out.append("(( Hover for info ))").event((HoverEvent) null).color(ChatColor.GRAY).italic(true).create());
        break;

      case "queues": {
        if (RiftQueue.getQueues().isEmpty()) {
          out.append("No rift queues detected.").color(ChatColor.RED);
        } else {
          out.append("Queues:").color(ChatColor.GREEN).append("\n");
          for (final RiftQueue queue : RiftQueue.getQueues()) {
            final ComponentBuilder comp = new ComponentBuilder(queue.getName()).color(ChatColor.YELLOW).event(
              new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                             new ComponentBuilder(queue.getName() + " queue:").color(ChatColor.YELLOW)
                               .append("\n\n")
                               .append("Queueing:").color(ChatColor.AQUA)
                               .append(queue.isQueuing() ? " true" : " false")
                               .color(queue.isQueuing() ? ChatColor.GREEN : ChatColor.RED)
                               .append("\n")
                               .append("Players: ").color(ChatColor.AQUA)
                               .append("" + queue.getPriorityQueue().size()).color(ChatColor.WHITE)
                               .create()
              )
            );

            out.append(comp.create()).append("\n");
          }
        }

        sender.sendMessage(out.append("(( Hover for info ))").event((HoverEvent) null).color(ChatColor.GRAY).italic(true).create());
        break;
      }

      case "toggle":
        if (args.length != 2) {
          sender.sendMessage("Usage: /rift toggle <queue>");
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

        if (riftServer.getQueue().getName().equalsIgnoreCase(riftServer.getQueue().getDisplayName())) {
          out.append("Queue for " + riftServer.getQueue().getName() + " has been ").color(ChatColor.YELLOW);
        } else {
          out.append("Queue for (" + riftServer.getQueue().getName() + ") ").color(ChatColor.YELLOW).append(riftServer.getQueue().getDisplayName()).append(" has been ").color(ChatColor.YELLOW);
        }

        if (queuing) {
          out.append("unpaused").color(ChatColor.GREEN);
        } else {
          out.append("paused").color(ChatColor.RED);
        }

        out.append(".").color(ChatColor.YELLOW);

        sender.sendMessage(out.create());
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
