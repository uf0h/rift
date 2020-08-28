package me.ufo.rift.permission;

import java.util.UUID;

@FunctionalInterface
public interface IPriorityProvider {

  void send(final String source, final UUID uuid);

}
