package me.ufo.rift.tasks;

import me.ufo.rift.QueuePlayer;
import me.ufo.rift.Rift;
import me.ufo.rift.RiftQueue;
import me.ufo.rift.RiftServerStatus;
import me.ufo.rift.servers.RiftServer;
import me.ufo.rift.util.FastUUID;

public final class QueuePushTask implements Runnable {

    private final Rift plugin;

    public QueuePushTask(final Rift plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.plugin.info("running push task");
        for (final RiftQueue queue : RiftQueue.getQueues()) {
            this.plugin.info(queue.toString());

            if (!queue.hasDestinationServer()) {
                continue;
            }

            if (!queue.isQueuing()) {
                continue;
            }

            final RiftServer riftServer = RiftServer.get(queue.getName());
            if (riftServer == null) {
                continue;
            }
            this.plugin.info(riftServer.toString());

            if (!riftServer.isOnline()) {
                continue;
            }

            if (riftServer.getServerStatus() != RiftServerStatus.ONLINE) {
                continue;
            }

            final QueuePlayer player = queue.getPriorityQueue().poll();

            if (player != null) {
                this.plugin.redis().async(player.getCurrent(), "QUEUE_SEND",
                    FastUUID.toString(player.getUuid()) + "," + player.getDestination()
                );
            }

            QueuePlayer.getPlayers().remove(player);
        }
    }

}
