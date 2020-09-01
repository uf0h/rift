package me.ufo.rift.listeners;

import me.ufo.rift.Rift;
import me.ufo.rift.queues.QueuePlayer;
import me.ufo.rift.redis.Riftbound;
import me.ufo.rift.server.RiftServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public final class RiftServerListener implements Listener {

  private final Rift plugin;

  public RiftServerListener(final Rift plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onServerConnect(final ServerConnectEvent event) {
    this.plugin.info("ServerConnectEvent triggered.");

    final RiftServer to = RiftServer.fromName(event.getTarget().getName());

    if (to != null) {
      if (to.isHubServer()) {
        event.setTarget(this.plugin.getLeastPopulatedHub());
      }
    }
  }

  // when player is kicked from server
  // can't distinguish between ban and kick
  @EventHandler
  public void onServerKick(final ServerKickEvent event) {
    // TODO: maybe check reason for "ban" then ignore

    this.plugin.info("ServerKickEvent triggered.");

    final RiftServer from = RiftServer.fromName(event.getKickedFrom().getName());

    if (from != null) {
      if (from.isDestinationServer()) {
        event.setCancelServer(this.plugin.getLeastPopulatedHub());
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onServerSwitch(final ServerSwitchEvent event) {
    if (event.getFrom() == null) {
      return;
    }

    this.plugin.info("ServerSwitchEvent triggered.");
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

    this.plugin.info("ProxyDisconnectEvent triggered.");
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
