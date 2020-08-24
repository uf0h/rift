package me.ufo.rift;

import java.util.Arrays;
import me.ufo.rift.redis.RiftboundMessageEvent;
import me.ufo.rift.servers.RiftServer;
import me.ufo.rift.servers.RiftServerType;
import me.ufo.rift.util.FastUUID;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;


public final class RiftboundMessageListener implements Listener {

    private final Rift plugin;

    public RiftboundMessageListener(final Rift plugin) {
        this.plugin = plugin;
    }


    // TODO: async
    @EventHandler
    public void onRiftMessageReceive(final RiftboundMessageEvent event) {
        if (event.getSource().equalsIgnoreCase("rift:bungee")) {
            if (this.plugin.debug()) {
                this.plugin.info(
                    "Received riftboundmessage from this server: {source: " + event.getSource() +
                        ", action: " + event.getAction() +
                        ", message: " + Arrays.toString(event.getMessage()) + "}"
                );
            }
            return;
        }

        if (!"PING".equalsIgnoreCase(event.getAction())) {
            this.plugin.info(
                "Received riftboundmessage: {source: " + event.getSource() +
                    ", action: " + event.getAction() +
                    ", message: " + Arrays.toString(event.getMessage()) + "}"
            );
        }

        if ("PLAYER_INFO_REQUEST".equalsIgnoreCase(event.getAction())) {
            //this.plugin.provider().send(event.getSource(), FastUUID.fromString(event.getMessage()[0]));
        }
        // ... received when a player is allowed to join a queue
        else if ("QUEUE_JOIN".equalsIgnoreCase(event.getAction())) {
            final RiftServer server = RiftServer.get(event.getMessage()[0]);
            final RiftQueue queue = RiftQueue.get(server.getName());

            if (queue.hasDestinationServer()) {
                final QueuePlayer queuePlayer = new QueuePlayer(
                    FastUUID.fromString(event.getMessage()[1]), event.getSource(), server.getName()
                );
                queuePlayer.setRank(event.getMessage()[2]);
                queuePlayer.setPriority(Integer.parseInt(event.getMessage()[3]));

                queue.getPriorityQueue().add(queuePlayer);

                this.plugin.info("Adding " + queuePlayer.getUuid() + " to queue " + queue.getName());
            }
        } else if ("QUEUE_LEAVE".equalsIgnoreCase(event.getAction())) {
            final QueuePlayer player = QueuePlayer.get(FastUUID.fromString(event.getMessage()[1]));

            QueuePlayer.getPlayers().remove(player);
        }
        // ... received while server is online
        else if ("PING".equalsIgnoreCase(event.getAction())) {
            RiftServer riftServer = RiftServer.get(event.getSource());
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
                                "Associated queue for destination server {" + event.getSource() + "} enabled."
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
        // ... received when server goes online
        else if ("ONLINE".equalsIgnoreCase(event.getAction())) {
            RiftServer riftServer = RiftServer.get(event.getSource());
            // ... server is back online
            if (riftServer != null) {
                // TODO: check if whitelist from message
                riftServer.setServerStatus(RiftServerStatus.ONLINE);

                // enable queue if found
                if (riftServer.hasQueue()) {
                    if (!riftServer.getQueue().hasDestinationServer()) {
                        riftServer.getQueue().hasDestinationServer(true);
                        riftServer.getQueue().setQueuing(true);
                        if (this.plugin.debug()) {
                            this.plugin.info(
                                "Associated queue for destination server {" + event
                                    .getSource() + "} enabled."
                            );
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
        }
        // ... received when server goes offline
        else if ("OFFLINE".equalsIgnoreCase(event.getAction())) {
            final RiftServer riftServer = RiftServer.get(event.getSource());

            if (riftServer != null) {
                riftServer.setServerStatus(RiftServerStatus.OFFLINE);

                if (riftServer.isDestinationServer()) {
                    riftServer.getQueue().setQueuing(false);
                }
            }
        }
    }

}
