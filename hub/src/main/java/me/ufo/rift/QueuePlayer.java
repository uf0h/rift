package me.ufo.rift;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class QueuePlayer {

    private final static Set<QueuePlayer> PLAYERS = new HashSet<>();

    private final UUID uuid;
    private final String destination;
    private final long insertion;

    private String rank;
    private int priority = 100;

    public QueuePlayer(final UUID uuid, final String destination) {
        this.uuid = uuid;
        this.destination = destination;
        this.insertion = System.currentTimeMillis();

        PLAYERS.add(this);
    }

    public String getDestination() {
        return destination;
    }

    public long getInsertionTime() {
        return insertion;
    }

    public String getRank() {
        return this.rank;
    }

    public void setRank(final String rank) {
        this.rank = rank;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(final int priority) {
        this.priority = priority;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public static QueuePlayer get(final UUID uuid) {
        for (final QueuePlayer player : PLAYERS) {
            if (player.getUuid().equals(uuid)) {
                return player;
            }
        }

        return null;
    }

    public static Set<QueuePlayer> getPlayers() {
        return PLAYERS;
    }

    @Override
    public String toString() {
        return "QueuePlayer{" +
            "uuid=" + uuid +
            ", destination='" + destination + '\'' +
            ", insertion=" + insertion +
            ", rank='" + rank + '\'' +
            ", priority=" + priority +
            '}';
    }

}
