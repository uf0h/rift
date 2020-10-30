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
      PLAYER_CHANGE_SERVER,
      PLAYER_PROXY_DISCONNECT,
      PLAYER_INFO_RESPONSE,
      SERVER_RESTARTING;

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
      PLAYER_INFO_REQUEST,
      PLAYER_QUEUE_JOIN,
      PLAYER_QUEUE_LEAVE,
      PLAYER_QUEUE_BYPASS;
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

    /**
     * Queue join action {@link Outbound.Action#PLAYER_QUEUE_JOIN}.
     * <p>
     * Sent to bungee server...
     *
     * @param uuid        uniqueId of player
     * @param destination server queue to join
     * @param rank        rank of player
     * @param priority    queue priority of player
     */
    public void playerQueueJoin(final UUID uuid, final String destination, final String rank,
                                final int priority) {

      Rift.instance().redis().async(
        // Channel
        Server.BUNGEE.name(),
        // Action
        Action.PLAYER_QUEUE_JOIN.name(),
        // Message
        FastUUID.toString(uuid) + "," + destination + "," + rank + "," + priority
      );
    }

    public void playerQueueLeave(final UUID uuid, final String destination) {
      Rift.instance().redis().async(
        // Channel
        Server.BUNGEE.name(),
        // Action
        Action.PLAYER_QUEUE_LEAVE.name(),
        // Message
        FastUUID.toString(uuid) + "," + destination
      );
    }

    public void playerQueueBypass(final UUID uuid, final String destination) {
      Rift.instance().redis().async(
        // Destination
        Server.BUNGEE.name(),
        // Action
        Action.PLAYER_QUEUE_BYPASS.name(),
        // Message
        FastUUID.toString(uuid) + "," + destination
      );
    }

  }

}
