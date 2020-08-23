package me.ufo.rift.permission;

import java.util.UUID;

@FunctionalInterface
public interface IPermissionProvider {

    void send(final String source, final UUID uuid);

}
