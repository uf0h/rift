package me.ufo.rift.permission;

import me.ufo.rift.Rift;
import me.ufo.rift.permission.impl.LuckPerms;

public enum PriorityProvider {

  LUCKPERMS("LuckPerms", new LuckPerms());

  private final String name;
  private final IPriorityProvider provider;

  private static IPriorityProvider currentProvider;

  PriorityProvider(final String name, final IPriorityProvider provider) {
    this.name = name;
    this.provider = provider;
  }

  public static void setup(final Rift plugin) {
    currentProvider = _setup(plugin);
  }

  public static IPriorityProvider get() {
    return currentProvider;
  }

  private static IPriorityProvider _setup(final Rift plugin) {
    for (final PriorityProvider value : values()) {
      if (plugin.getServer().getPluginManager().getPlugin(value.name) != null) {
        return value.provider;
      }
    }

    return null;
  }

}
