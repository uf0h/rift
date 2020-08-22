package me.ufo.rift;

import java.util.concurrent.ThreadLocalRandom;
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

        if (args.length !=  1) {
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "debug":
                final boolean mode = this.plugin.toggleDebug();
                if (sender instanceof Player) {
                    sender.sendMessage(mode ? "Debug mode is enabled." : "Debug mode is disabled.");
                }

                this.plugin.debug(mode ? "Debug mode is enabled." : "Debug mode is disabled.");
                break;
            case "test":
                this.plugin.redis().async("hub-1", "PING", "async" +
                    ThreadLocalRandom.current().nextInt(0, 300));
                this.plugin.redis().sync("hub-1", "PING", "sync" +
                    ThreadLocalRandom.current().nextInt(0, 300));
                break;
            default:
                return false;
        }

        return true;
    }

}
