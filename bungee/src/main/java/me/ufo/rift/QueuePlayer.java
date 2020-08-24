package me.ufo.rift;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class QueuePlayer implements Comparable<QueuePlayer> {

    private final static Set<QueuePlayer> PLAYERS = new HashSet<>();

    private final UUID uuid;
    private final String current;
    private final String destination;
    private final long insertion;

    private String rank;
    private int priority = 100;

    public QueuePlayer(final UUID uuid, final String current, final String destination) {
        this.uuid = uuid;
        this.current = current;
        this.destination = destination;
        this.insertion = System.currentTimeMillis();

        PLAYERS.add(this);
    }

    public String getCurrent() {
        return this.current;
    }

    public String getDestination() {
        return this.destination;
    }

    public long getInsertionTime() {
        return this.insertion;
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

    @Override
    public int compareTo(final QueuePlayer o) {
        if (o.getPriority() < this.getPriority()) {
            System.out.println("1");
            return 1;
        } else if (o.getPriority() > this.getPriority()) {
            System.out.println("2");
            return -1;
        } else {
            if (o.getInsertionTime() < this.getInsertionTime()) {
                System.out.println("3");
                return 1;
            } else {
                System.out.println("4");
                return -1;
            }
        }
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
            ", current='" + current + '\'' +
            ", destination='" + destination + '\'' +
            ", insertion=" + insertion +
            ", rank='" + rank + '\'' +
            ", priority=" + priority +
            '}';
    }

}
