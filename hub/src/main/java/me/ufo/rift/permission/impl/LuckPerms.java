package me.ufo.rift.permission.impl;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.ufo.rift.Rift;
import me.ufo.rift.obj.QueuePlayer;
import me.ufo.rift.permission.IPriorityProvider;
import me.ufo.rift.redis.Riftbound;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedDataManager;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class LuckPerms implements IPriorityProvider {

  private final net.luckperms.api.LuckPerms api;
  private final Map<String, ImmutableContextSet> contexts;

  public LuckPerms() {
    this.api = LuckPermsProvider.get();
    this.contexts = new ConcurrentHashMap<>();
  }

  @Override
  public void check(final UUID uuid, final String destination) {
    final User user = api.getUserManager().getUser(uuid);

    if (user == null) {
      Rift.instance().info("Error getting player for priority provider.");
      QueuePlayer queuePlayer = QueuePlayer.fromUUID(uuid);
      if (queuePlayer == null) {
        queuePlayer = new QueuePlayer(uuid, destination);
      }

      //queuePlayer.setRank(rank);
      queuePlayer.setPriority(1);

      final Player bukkitPlayer = Bukkit.getPlayer(uuid);

      bukkitPlayer.sendMessage(
        ChatColor.RED.toString() + "Attempting to send " + bukkitPlayer.getName() + " to " + queuePlayer
          .getDestination() + ".");

      Riftbound.outbound().playerQueueJoin(
        uuid,
        queuePlayer.getDestination(),
        queuePlayer.getRank(),
        queuePlayer.getPriority()
      );
      return;
    }

    ImmutableContextSet cs = contexts.get(destination);

    if (cs == null) {
      cs = contexts.put(destination, ImmutableContextSet.of("server", destination));

      if (cs == null) {
        cs = api.getContextManager().getStaticContext();
      }
    }

    final QueryOptions qo = QueryOptions.contextual(cs).toBuilder().build();
    final CachedDataManager cachedData = user.getCachedData();
    final CachedPermissionData permissionData = cachedData.getPermissionData(qo);

    if (permissionData.checkPermission("rift.bypass").asBoolean()) {
      Riftbound.outbound().playerQueueBypass(uuid, destination);
    } else {
      int priority = 100;
      for (int i = 100; i > 0; i--) {
        if (permissionData.checkPermission("rift.priority." + i).asBoolean()) {
          priority = i;
          break;
        }
      }

      QueuePlayer queuePlayer = QueuePlayer.fromUUID(uuid);
      if (queuePlayer == null) {
        queuePlayer = new QueuePlayer(uuid, destination);
      }

      //queuePlayer.setRank(rank);
      queuePlayer.setPriority(priority);

      final Player bukkitPlayer = Bukkit.getPlayer(uuid);

      bukkitPlayer.sendMessage(
        ChatColor.RED.toString() + "Attempting to send " + bukkitPlayer.getName() + " to " + queuePlayer
          .getDestination() + ".");

      Riftbound.outbound().playerQueueJoin(
        uuid,
        queuePlayer.getDestination(),
        queuePlayer.getRank(),
        queuePlayer.getPriority()
      );
    }
  }

}
