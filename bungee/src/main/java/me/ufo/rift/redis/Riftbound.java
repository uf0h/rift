package me.ufo.rift.redis;

import java.util.UUID;
import me.ufo.rift.Rift;
import me.ufo.rift.util.FastUUID;

public final class Riftbound {

  private final static Inbound inbound = new Inbound();
  private final static Outbound outbound = new Outbound();

  public enum Server {
    BUNGEE,
    ALL
  }

  public static Inbound inbound() {
    return inbound;
  }

  public static Outbound outbound() {
    return outbound;
  }

  public final static class Inbound {

    public enum Action {
      PING,
      PLAYER_QUEUE_LEAVE,
      PLAYER_QUEUE_JOIN,
      PLAYER_HUB_SEND,
      PLAYER_QUEUE_BYPASS
    }

  }

  public final static class Outbound {

    public enum Action {
      PLAYER_CHANGE_SERVER,
      PLAYER_PROXY_DISCONNECT
    }

    /**
     * Player proxy disconnect action {@link Action#}.
     * <p>
     * Sent to server player disconnected from...
     *
     * @param uuid          uniqueId of player
     * @param currentServer last server of player
     */
    public void playerProxyDisconnect(final UUID uuid, final String currentServer) {
      Rift.instance().redis().async(
        // Channel
        currentServer,
        // Action
        Action.PLAYER_PROXY_DISCONNECT.name(),
        // Message
        FastUUID.toString(uuid)
      );
    }

    public void playerChangeServer(final UUID uuid, final String currentServer) {
      Rift.instance().redis().async(
        // Channel
        currentServer,
        // Action
        Action.PLAYER_CHANGE_SERVER.name(),
        // Message
        FastUUID.toString(uuid));
    }

  }

}
