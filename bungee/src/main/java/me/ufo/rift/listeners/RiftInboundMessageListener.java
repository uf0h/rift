package me.ufo.rift.listeners;

import java.util.Arrays;
import java.util.UUID;
import me.ufo.rift.Rift;
import me.ufo.rift.events.RiftInboundMessageEvent;
import me.ufo.rift.queues.QueuePlayer;
import me.ufo.rift.queues.RiftQueue;
import me.ufo.rift.redis.Riftbound;
import me.ufo.rift.servers.RiftServer;
import me.ufo.rift.servers.RiftServerStatus;
import me.ufo.rift.servers.RiftServerType;
import me.ufo.rift.util.FastUUID;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;


public final class RiftInboundMessageListener implements Listener {

  private final Rift plugin;

  public RiftInboundMessageListener(final Rift plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onRiftMessageReceive(final RiftInboundMessageEvent event) {
    this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
      if (this.plugin.debug()) {
        this.plugin.info(
          "Received riftboundmessage: {source: " + event.getSource() +
          ", action: " + event.getAction() +
          ", message: " + Arrays.toString(event.getMessage()) + "}"
        );
      }

      if (event.getAction() == Riftbound.Inbound.Action.PING) {
        this.ping(event);
        return;
      }

      final UUID uuid = FastUUID.fromString(event.getMessage()[0]);

      switch (event.getAction()) {
        case PLAYER_QUEUE_JOIN: {
          final RiftServer server = RiftServer.fromName(event.getMessage()[1]);
          final RiftQueue queue = RiftQueue.fromName(server.getName());

          // TODO: destinationServer not needed?
          if (queue.hasDestinationServer()) {
            final QueuePlayer queuePlayer = new QueuePlayer(uuid, event.getSource(), server.getName());

            queuePlayer.setRank(event.getMessage()[2]);
            queuePlayer.setPriority(Integer.parseInt(event.getMessage()[3]));

            queue.getPriorityQueue().add(queuePlayer);
          }
          break;
        }

        case PLAYER_QUEUE_LEAVE: {
          QueuePlayer.destroy(uuid);
          break;
        }

        case PLAYER_HUB_SEND: {
          this.plugin.getProxy().getPlayer(uuid).connect(this.plugin.getLeastPopulatedHub());
          break;
        }

        case PLAYER_QUEUE_BYPASS: {
          this.plugin.getProxy().getPlayer(uuid)
            .connect(this.plugin.getProxy().getServerInfo(event.getMessage()[1]));
          break;
        }
        default:
      }
    });
  }

  private void ping(final RiftInboundMessageEvent event) {
    RiftServer riftServer = RiftServer.fromName(event.getSource());
    if (riftServer != null) {
      // TODO: check if whitelist from message
      riftServer.setServerStatus(RiftServerStatus.ONLINE);
      riftServer.setOnlinePlayers(Integer.parseInt(event.getMessage()[1]));

      // enable queue if found
      if (riftServer.hasQueue()) {
        if (!riftServer.getQueue().hasDestinationServer()) {
          riftServer.getQueue().hasDestinationServer(true);
          if (this.plugin.debug()) {
            this.plugin.info(
              "Associated queue for destination server {" + event
                .getSource() + "} enabled."
            );
          }
        }
      } else {
        if (riftServer.isDestinationServer()) {
          riftServer.attachQueue(event.getSource());
          if (this.plugin.debug()) {
            this.plugin.info("Creating new queue for {" + event.getSource() + "}.");
          }
        }
      }
    } else {
      riftServer =
        new RiftServer(event.getSource(), RiftServerType.valueOf(event.getMessage()[0]));

      // TODO: check if whitelist from message
      riftServer.setServerStatus(RiftServerStatus.ONLINE);

      if (riftServer.isDestinationServer()) {
        riftServer.attachQueue(event.getSource());
        if (this.plugin.debug()) {
          this.plugin.info("Creating new queue for {" + event.getSource() + "}.");
        }
      }
    }

    riftServer.ping();
  }

}
