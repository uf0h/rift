package me.ufo.rift;

import java.util.concurrent.ThreadLocalRandom;
import me.ufo.rift.util.FastUUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        switch (args[0].toLowerCase()) {
            case "joinqueue":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Player only command.");
                    return false;
                }

                if (args.length != 2) {
                    sender.sendMessage("Usage: /rift joinqueue <queue>");
                    return false;
                }

                // TODO: have list of players that have joined a queue to check dupe joining

                this.plugin.redis().async(
                    args[1], "PLAYER_INFO_REQUEST", FastUUID.toString(((Player) sender).getUniqueId())
                );
                break;

            case "leavequeue":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Player only command.");
                    return false;
                }

                if (args.length != 2) {
                    sender.sendMessage("Usage: /rift leavequeue <queue>");
                    return false;
                }

                final QueuePlayer queuePlayer = QueuePlayer.get(((Player) sender).getUniqueId());
                if (queuePlayer != null) {
                    this.plugin.redis().async(
                        "bungee",
                        "QUEUE_LEAVE",
                        queuePlayer.getDestination() + "," + FastUUID.toString(queuePlayer.getUuid())
                    );

                    QueuePlayer.getPlayers().remove(queuePlayer);
                } else {
                    sender.sendMessage("You are not in a queue.");
                }
                break;

            case "debugmode":
                final boolean mode = this.plugin.toggleDebug();

                sender.sendMessage(mode ? "Debug mode is enabled." : "Debug mode is disabled.");
                break;

            case "debug":
                if (sender instanceof Player) {
                    final Player player = (Player) sender;
                    sender.sendMessage("" + QueuePlayer.getPlayers().size());
                    sender.sendMessage("contained: " + (QueuePlayer.get(((Player) sender).getUniqueId()) != null));
                    for (final QueuePlayer queuePlayer1 : QueuePlayer.getPlayers()) {
                        sender.sendMessage(queuePlayer1.getUuid().toString());
                        sender.sendMessage(player.getUniqueId().toString());
                        sender.sendMessage("equals: " + (queuePlayer1.getUuid().equals(player.getUniqueId())));
                    }
                }
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
                this.plugin.redis().async("all", "PING", this.plugin.response());
                break;

            default:
                return false;
        }

        return true;
    }

}
