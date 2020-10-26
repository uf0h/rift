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
      PLAYER_INFO_REQUEST;

      private static final Action[] values = Action.values();

      public static Action get(final String in) {
        for (final Action value : values) {
          if (in.equalsIgnoreCase(value.name())) {
            return value;
          }
        }

        return null;
      }

    }

  }

  public final static class Outbound {

    public enum Action {
      PING,
      PLAYER_INFO_RESPONSE,
      PLAYER_HUB_SEND,
      PLAYER_SPECIFIC_HUB_SEND,
      PLAYER_QUEUE_BYPASS
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

    public void playerHubSend(final UUID uuid, final boolean async) {
      if (async) {
        Rift.instance().redis().async(
          // Destination
          Server.BUNGEE.name(),
          // Action
          Action.PLAYER_HUB_SEND.name(),
          // Message
          FastUUID.toString(uuid)
        );
      } else {
        Rift.instance().redis().sync(
          // Destination
          Server.BUNGEE.name(),
          // Action
          Action.PLAYER_HUB_SEND.name(),
          // Message
          FastUUID.toString(uuid)
        );
      }
    }

    public void playerHubSend(final UUID uuid, final String server, final boolean async) {
      if (async) {
        Rift.instance().redis().async(
          // Destination
          Server.BUNGEE.name(),
          // Action
          Action.PLAYER_SPECIFIC_HUB_SEND.name(),
          // Message
          FastUUID.toString(uuid) + "," + server
        );
      } else {
        Rift.instance().redis().sync(
          // Destination
          Server.BUNGEE.name(),
          // Action
          Action.PLAYER_SPECIFIC_HUB_SEND.name(),
          // Message
          FastUUID.toString(uuid) + "," + server
        );
      }
    }

    public void playerQueueBypass(final UUID uuid) {
      Rift.instance().redis().async(
        // Destination
        Server.BUNGEE.name(),
        // Action
        Action.PLAYER_QUEUE_BYPASS.name(),
        // Message
        FastUUID.toString(uuid) + "," + Rift.instance().name()
      );
    }

  }

}
