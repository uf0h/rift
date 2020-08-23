package me.ufo.rift.permission;

import java.util.UUID;
import me.ufo.rift.Rift;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedPermissionData;

public enum PermissionProvider implements IPermissionProvider {

    LUCKPERMS("LuckPerms") {
        @Override
        public void send(final String source, final UUID uuid) {
            LuckPermsProvider.get().getUserManager().loadUser(uuid).thenAcceptAsync(u -> {
                final CachedPermissionData permissionData = u.getCachedData().getPermissionData();

                int priority = 100;
                for (int i = 100; i > 0; i--) {
                    if (permissionData.checkPermission("rift.priority." + i).asBoolean()) {
                        priority = i;
                        break;
                    }
                }

                Rift.instance().redis()
                    .async(source, "PLAYER_INFO_RESPONSE", u.getPrimaryGroup() + "," + priority);
            });
        }
    };

    private final String name;

    PermissionProvider(final String name) {
        this.name = name;
    }

    public static PermissionProvider setup(final Rift plugin) {
        for (final PermissionProvider value : values()) {
            if (plugin.getServer().getPluginManager().getPlugin(value.name) != null) {
                return value;
            }
        }

        return null;
    }

}
