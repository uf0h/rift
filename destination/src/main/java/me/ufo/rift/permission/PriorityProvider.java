package me.ufo.rift.permission;

import java.util.UUID;
import me.ufo.rift.Rift;
import me.ufo.rift.redis.Riftbound;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedPermissionData;

public enum PriorityProvider implements IPriorityProvider {

  LUCKPERMS("LuckPerms") {
    @Override
    public void send(final String source, final UUID uuid) {
      LuckPermsProvider.get().getUserManager().loadUser(uuid).thenAcceptAsync(u -> {
        final CachedPermissionData permissionData = u.getCachedData().getPermissionData();

        if (permissionData.checkPermission("rift.bypass").asBoolean()) {
          Riftbound.outbound().playerQueueBypass(uuid);
        } else {
          int priority = 100;
          for (int i = 100; i > 0; i--) {
            if (permissionData.checkPermission("rift.priority." + i).asBoolean()) {
              priority = i;
              break;
            }
          }

          Riftbound.outbound().playerInfoResponse(uuid, source, u.getPrimaryGroup(), priority);
        }
      });
    }
  };

  private final String name;

  PriorityProvider(final String name) {
    this.name = name;
  }

  public static PriorityProvider setup(final Rift plugin) {
    for (final PriorityProvider value : values()) {
      if (plugin.getServer().getPluginManager().getPlugin(value.name) != null) {
        return value;
      }
    }

    return null;
  }

}
