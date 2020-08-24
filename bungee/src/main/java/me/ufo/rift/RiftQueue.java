package me.ufo.rift;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public final class RiftQueue {

    private final static List<RiftQueue> QUEUES = new ArrayList<>(3);

    // name is also destination
    private final String name;

    private final PriorityQueue<QueuePlayer> priorityQueue;

    // whether the destination server is found
    private boolean destination;

    // whether the queue is queuing and pushing players destination server
    private boolean queuing;

    public RiftQueue(final String name) {
        this.name = name;
        this.priorityQueue = new PriorityQueue<>(QueuePlayer::compareTo);
        this.queuing = true;

        QUEUES.add(this);
    }

    public String getName() {
        return this.name;
    }

    public PriorityQueue<QueuePlayer> getPriorityQueue() {
        return this.priorityQueue;
    }

    public int getPosition(final QueuePlayer player) {
        if (!this.priorityQueue.contains(player)) {
            return -1;
        }

        final PriorityQueue<QueuePlayer> cloned = new PriorityQueue<>(QueuePlayer::compareTo);
        cloned.addAll(this.priorityQueue);

        int position = 0;
        while (!cloned.isEmpty()) {
            position++;

            if (cloned.poll().getUuid().equals(player.getUuid())) {
                return position;
            }
        }

        return position;
    }

    public boolean hasDestinationServer() {
        return this.destination;
    }

    public void hasDestinationServer(final boolean destination) {
        this.destination = destination;
    }

    public boolean isQueuing() {
        return this.queuing;
    }

    public void setQueuing(final boolean queuing) {
        this.queuing = queuing;
    }

    public static RiftQueue get(final String name) {
        for (final RiftQueue queue : QUEUES) {
            if (queue.getName().equalsIgnoreCase(name)) {
                return queue;
            }
        }

        return null;
    }

    public static List<RiftQueue> getQueues() {
        return QUEUES;
    }

    @Override
    public String toString() {
        return "RiftQueue{" +
            "name='" + name + '\'' +
            ", priorityQueue=" + priorityQueue.size() +
            ", destination=" + destination +
            ", queuing=" + queuing +
            '}';
    }

}
