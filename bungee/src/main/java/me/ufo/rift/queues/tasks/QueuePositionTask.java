package me.ufo.rift.queues.tasks;

import me.ufo.rift.Rift;
import me.ufo.rift.queues.QueuePlayer;
import me.ufo.rift.queues.RiftQueue;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public final class QueuePositionTask implements Runnable {

  private final Rift plugin;

  private final BaseComponent[] pausedQueue;

  public QueuePositionTask(final Rift plugin) {
    this.plugin = plugin;
    this.pausedQueue = new ComponentBuilder("Queue is paused.").create();
  }

  @Override
  public void run() {
    for (final RiftQueue queue : RiftQueue.getQueues()) {
      for (final QueuePlayer queuePlayer : queue.getPriorityQueue()) {
        final ProxiedPlayer proxiedPlayer = this.plugin.getProxy().getPlayer(queuePlayer.getUuid());
        final int position = queue.getPosition(queuePlayer);
        proxiedPlayer.sendMessage("Current position in queue for " + queue.getName() + ": " + position);

        if (!queue.hasDestinationServer() || !queue.isQueuing()) {
          proxiedPlayer.sendMessage(this.pausedQueue);
        }
      }
    }
  }

}
