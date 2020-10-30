package me.ufo.rift.permission;

import java.util.UUID;

@FunctionalInterface
public interface IPriorityProvider {

  void check(final UUID uuid, final String destination);

}
