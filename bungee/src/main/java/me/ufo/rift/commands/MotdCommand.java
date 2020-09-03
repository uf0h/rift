package me.ufo.rift.commands;

import java.util.Arrays;
import me.ufo.rift.Rift;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

public final class MotdCommand extends Command {

  private final Rift plugin;

  public MotdCommand(final Rift plugin) {
    super("motd", "rift.command");
    this.plugin = plugin;
  }

  @Override
  public void execute(final CommandSender sender, final String[] args) {
    if (!sender.hasPermission("rift.command")) {
      return;
    }


    if (args.length < 1) {
      sender.sendMessage(new ComponentBuilder().color(ChatColor.RED).append("Usage:")
                           .append("\n")
                           .color(ChatColor.RED).append("/motd get")
                           .append("\n")
                           .color(ChatColor.RED).append("/motd 1 <motd>")
                           .append("\n")
                           .color(ChatColor.RED).append("/motd 2 <motd>").create());
      return;
    }

    if ("1".equalsIgnoreCase(args[0])) {
      final String motdLine = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
      sender.sendMessage(this.plugin.config().setMotdLineOne(motdLine));
    } else if ("2".equalsIgnoreCase(args[0])) {
      final String motdLine = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
      sender.sendMessage(this.plugin.config().setMotdLineTwo(motdLine));
    } else if ("get".equalsIgnoreCase(args[0])) {
      sender.sendMessage(this.plugin.config().getMotd());
    }
  }

}
