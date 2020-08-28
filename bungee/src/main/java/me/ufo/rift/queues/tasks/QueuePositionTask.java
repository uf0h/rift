package me.ufo.rift.queues.tasks;

import me.ufo.rift.Rift;
import me.ufo.rift.queues.QueuePlayer;
import me.ufo.rift.queues.RiftQueue;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public final class QueuePositionTask implements Runnable {

  private final Rift plugin;

  public QueuePositionTask(final Rift plugin) {
    this.plugin = plugin;
  }

  @Override
  public void run() {
    for (final RiftQueue queue : RiftQueue.getQueues()) {
      final BaseComponent[] paused = TextComponent
        .fromLegacyText(this.plugin.config().getQueuePaused().replace("%server%", queue.getDisplayName()));

      for (final QueuePlayer queuePlayer : queue.getPriorityQueue()) {
        final ProxiedPlayer proxiedPlayer = this.plugin.getProxy().getPlayer(queuePlayer.getUuid());
        final int position = queue.getPosition(queuePlayer);
        proxiedPlayer.sendMessage(new TextComponent(
          this.plugin.config().getQueuePosition()
            .replace("%pos%", "" + position)
            .replace("%total%", "" + queue.getPriorityQueue().size())
            .replace("%server%", queue.getDisplayName()))
        );

        if (!queue.hasDestinationServer() || !queue.isQueuing()) {
          proxiedPlayer.sendMessage(paused);
        }
      }
    }
  }

}
