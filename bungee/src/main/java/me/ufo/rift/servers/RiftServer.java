package me.ufo.rift.servers;

import java.util.ArrayList;
import java.util.List;
import me.ufo.rift.Rift;
import me.ufo.rift.RiftQueue;
import me.ufo.rift.RiftServerStatus;

public final class RiftServer {

    private final static List<RiftServer> SERVERS = new ArrayList<>(5);

    private final String name;
    private final RiftServerType serverType;
    private int onlinePlayers;
    private RiftServerStatus serverStatus;
    private long lastPing;

    private RiftQueue queue;

    public RiftServer(final String name, final RiftServerType serverType) {
        this.name = name;
        this.serverType = serverType;

        SERVERS.add(this);
    }

    public String getName() {
        return this.name;
    }

    public boolean isDestinationServer() {
        return this.serverType == RiftServerType.DESTINATION;
    }

    public boolean isHubServer() {
        return this.serverType == RiftServerType.HUB;
    }

    public RiftServerType getServerType() {
        return this.serverType;
    }

    public void setServerStatus(final RiftServerStatus serverStatus) {
        this.serverStatus = serverStatus;
    }

    public RiftServerStatus getServerStatus() {
        return serverStatus;
    }

    public int getOnlinePlayers() {
        return this.onlinePlayers;
    }

    public void setOnlinePlayers(final int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public long getLastPing() {
        return lastPing;
    }

    public void ping() {
        this.lastPing = System.currentTimeMillis();
    }

    public boolean isOnline() {
        return this.serverStatus == RiftServerStatus.ONLINE && System.currentTimeMillis() - this.lastPing < 15000L;
    }

    public boolean hasQueue() {
        return this.queue != null;
    }

    public RiftQueue getQueue() {
        return this.queue;
    }

    public RiftQueue attachQueue(final String name) {
        // if queue already exists but is not set
        for (final RiftQueue queue : RiftQueue.getQueues()) {
            if (queue.getName().equalsIgnoreCase(name)) {
                this.queue = queue;
                return this.queue;
            }
        }

        // set new queue
        this.queue = new RiftQueue(name);
        this.queue.hasDestinationServer(true);

        Rift.instance().config().saveQueue(this.queue);
        return this.queue;
    }

    public void removeQueue() {
        this.queue = null;
    }

    public static RiftServer get(final String name) {
        for (final RiftServer server : SERVERS) {
            if (server.getName().equalsIgnoreCase(name)) {
                return server;
            }
        }

        return null;
    }

    public static List<RiftServer> getServers() {
        return SERVERS;
    }

    @Override
    public String toString() {
        return "RiftServer{" +
            "name='" + name + '\'' +
            ", serverType=" + serverType +
            ", onlinePlayers=" + onlinePlayers +
            ", serverStatus=" + serverStatus +
            ", lastPing=" + lastPing +
            ", queue=" + queue +
            '}';
    }

}
