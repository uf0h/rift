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
      PLAYER_INFO_REQUEST,
    }

  }

  public final static class Outbound {

    public enum Action {
      PING,
      PLAYER_INFO_RESPONSE,
    }

    public void ping() {
      final String out = Rift.instance().response();

      Rift.instance().redis().async(
        // Channel
        Server.ALL.name(),
        // Action
        Action.PING.name(),
        // Message
        out
      );
    }

    public void playerInfoResponse(final UUID uuid, final String server, final String rank,
                                  final int priority) {
      Rift.instance().redis().async(
        // Destination
        server,
        // Action
        Action.PLAYER_INFO_RESPONSE.name(),
        // Message
        FastUUID.toString(uuid) + "," + rank + "," + priority
      );
    }

  }

}
