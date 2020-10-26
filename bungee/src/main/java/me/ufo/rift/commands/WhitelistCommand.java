package me.ufo.rift.commands;

import java.util.Iterator;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;
import me.ufo.rift.Rift;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public final class WhitelistCommand extends Command {

  private final Rift plugin;

  public WhitelistCommand(final Rift plugin) {
    super("bwhitelist", "whitelist.command", "bw");
    this.plugin = plugin;
  }

  @Override
  public void execute(final CommandSender sender, final String[] args) {
    if (!sender.hasPermission("whitelist.command")) {
      return;
    }

    if (args.length == 0) {
      sender.sendMessage(TextComponent.fromLegacyText("&cUsage: /whitelist <on|off|list|add|remove>"));
      return;
    }

    switch (args[0].toLowerCase()) {
      default: {
        return;
      }

      case "on": {
        this.plugin.setWhitelisted(true);
        sender.sendMessage("Turned on the whitelist.");
        break;
      }

      case "off": {
        this.plugin.setWhitelisted(false);
        sender.sendMessage("Turned off the whitelist.");
        break;
      }

      case "list": {
        final StringJoiner joiner = new StringJoiner(", ", "[", "]");

        for (final Map.Entry<UUID, String> entry : this.plugin.getWhitelistedPlayers().entrySet()) {
          joiner.add(entry.getValue());
        }

        sender.sendMessage(joiner.toString());
        break;
      }

      case "add": {
        if (args.length != 2) {
          sender.sendMessage("Usage: /whitelist add <player>");
          return;
        }
        final ProxiedPlayer player = this.plugin.getProxy().getPlayer(args[1]);

        if (player == null) {
          this.plugin.getWhitelistedPlayers().put(UUID.randomUUID(), args[1]);
        } else {
          this.plugin.getWhitelistedPlayers().put(player.getUniqueId(), player.getName());
        }

        this.plugin.config().saveWhitelistedPlayers();
        sender.sendMessage("Added " + args[1] + " to the whitelist.");
        break;
      }

      case "remove": {
        if (args.length != 2) {
          sender.sendMessage("Usage: /whitelist remove <player>");
          return;
        }

        final Iterator<Map.Entry<UUID, String>> iterator =
          this.plugin.getWhitelistedPlayers().entrySet().iterator();

        boolean found = false;
        while (iterator.hasNext()) {
          final Map.Entry<UUID, String> entry = iterator.next();

          if (args[1].equalsIgnoreCase(entry.getValue())) {
            found = true;
            iterator.remove();
            break;
          }
        }

        this.plugin.config().saveWhitelistedPlayers();
        if (!found) {
          sender.sendMessage("Could not remove " + args[1] + " from the whitelist.");
        } else {
          sender.sendMessage("Removed " + args[1] + " from the whitelist.");
        }
        break;
      }
    }
  }

}
