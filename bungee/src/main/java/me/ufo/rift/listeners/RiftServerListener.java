package me.ufo.rift.listeners;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.ufo.rift.Rift;
import me.ufo.rift.queues.QueuePlayer;
import me.ufo.rift.redis.Riftbound;
import me.ufo.rift.server.RiftServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public final class RiftServerListener implements Listener {

  private final Rift plugin;

  public RiftServerListener(final Rift plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onLoginEvent(final LoginEvent event) {
    if (this.plugin.isWhitelisted()) {
      final String name = event.getConnection().getName();
      final UUID uniqueId = event.getConnection().getUniqueId();

      final Iterator<Map.Entry<UUID, String>> iterator =
        this.plugin.getWhitelistedPlayers().entrySet().iterator();

      while (iterator.hasNext()) {
        final Map.Entry<UUID, String> entry = iterator.next();

        if (uniqueId == null) {
          this.plugin.getLogger().severe("UNiQUEID is null");
          return;
        }

        if (uniqueId.equals(entry.getKey())) {
          if (!name.equals(entry.getValue())) {
            this.plugin.getLogger().info("uuid true, name not true");
            iterator.remove();
            this.plugin.getWhitelistedPlayers().put(uniqueId, name);
            this.plugin.getProxy().getScheduler().schedule(this.plugin, () -> {
              this.plugin.config().saveWhitelistedPlayers();
            }, 5, TimeUnit.SECONDS);
          }
          return;
        }

        if (name.equalsIgnoreCase(entry.getValue())) {
          if (!uniqueId.equals(entry.getKey())) {
            this.plugin.getLogger().info("name true, uuid not true");
            iterator.remove();
            this.plugin.getWhitelistedPlayers().put(uniqueId, name);
            this.plugin.getProxy().getScheduler().schedule(this.plugin, () -> {
              this.plugin.config().saveWhitelistedPlayers();
            }, 5, TimeUnit.SECONDS);
          }
          return;
        }
      }

      event.setCancelled(true);
      event.setCancelReason(this.plugin.config().getWhitelistedMessage());
    }
  }

  @EventHandler
  public void onServerConnect(final ServerConnectEvent event) {
    if (this.plugin.debug()) {
      this.plugin.info("ServerConnectEvent triggered.");
    }

    final ProxiedPlayer player = event.getPlayer();

    if (player.getServer() == null) {
      event.setTarget(this.plugin.getLeastPopulatedHub());
    }
  }

  @EventHandler
  public void onServerSwitch(final ServerSwitchEvent event) {
    if (event.getFrom() == null) {
      return;
    }

    if (this.plugin.debug()) {
      this.plugin.info("ServerSwitchEvent triggered.");
    }
    this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
      final ProxiedPlayer proxiedPlayer = event.getPlayer();
      final RiftServer from = RiftServer.fromName(event.getFrom().getName());
      final RiftServer to = RiftServer.fromName(proxiedPlayer.getServer().getInfo().getName());

      if (to != null) {
        if (to.isHubServer()) {
          if (from != null) {
            // connecting to a hub server from another hub server
            if (from.isHubServer()) {
              // TODO:
            }
            // connecting to a hub server from a destination server
            else if (from.isDestinationServer()) {

            }
          }
        } else if (to.isDestinationServer()) {
          if (from != null) {
            // connecting to a destination server from a hub server:
            // connecting through a hub server is the only way to connect to a destination server
            if (from.isHubServer()) {
              Riftbound.outbound().playerChangeServer(proxiedPlayer.getUniqueId(), from.getName());
              QueuePlayer.destroy(proxiedPlayer.getUniqueId());
            }
          }
        }
      }
    });
  }

  @EventHandler
  public void onProxyDisconnect(final PlayerDisconnectEvent event) {
    if (event.getPlayer() == null) {
      return;
    }

    if (event.getPlayer().getServer() == null) {
      return;
    }

    if (this.plugin.debug()) {
      this.plugin.info("ProxyDisconnectEvent triggered.");
    }
    this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
      final ProxiedPlayer proxiedPlayer = event.getPlayer();
      final RiftServer riftServer = RiftServer.fromName(proxiedPlayer.getServer().getInfo().getName());

      if (riftServer != null) {
        // TODO: send to all hub servers
        if (riftServer.isHubServer()) {
          final QueuePlayer queuePlayer = QueuePlayer.fromUUID(proxiedPlayer.getUniqueId());
          if (queuePlayer != null) {
            Riftbound.outbound().playerProxyDisconnect(queuePlayer.getUuid(), riftServer.getName());
            queuePlayer.destroy();
          }
        }
      }
    });
  }

}
