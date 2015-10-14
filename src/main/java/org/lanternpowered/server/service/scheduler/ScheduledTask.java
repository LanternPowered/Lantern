package org.lanternpowered.server.service.scheduler;

import java.util.UUID;
import java.util.function.Consumer;

import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.scheduler.Task;

import com.google.common.base.MoreObjects;

/**
 * An internal representation of a {@link Task} created by a plugin.
 */
public class ScheduledTask implements Task {

    final long offset;
    final long period;
    final boolean delayIsTicks;
    final boolean intervalIsTicks;
    private final PluginContainer owner;
    private final Consumer<Task> executor;
    private long timestamp;
    private ScheduledTaskState state;
    private final UUID id;
    private final String name;
    private final TaskSynchronicity syncType;
    private final String stringRepresentation;

    // Internal Task state. Not for user-service use.
    public enum ScheduledTaskState {
        /**
         * Never ran before, waiting for the offset to pass.
         */
        WAITING(false),
        /**
         * In the process of switching to the running state.
         */
        SWITCHING(true),
        /**
         * Has ran, and will continue to unless removed from the task map.
         */
        RUNNING(true),
        /**
         * Task cancelled, scheduled to be removed from the task map.
         */
        CANCELED(false);

        public final boolean isActive;

        private ScheduledTaskState(boolean active) {
            this.isActive = active;
        }
    }

    ScheduledTask(TaskSynchronicity syncType, Consumer<Task> executor, String taskName, long delay, boolean delayIsTicks, long interval,
            boolean intervalIsTicks, PluginContainer pluginContainer) {
        // All tasks begin waiting.
        this.setState(ScheduledTaskState.WAITING);
        this.offset = delay;
        this.delayIsTicks = delayIsTicks;
        this.period = interval;
        this.intervalIsTicks = intervalIsTicks;
        this.owner = pluginContainer;
        this.executor = executor;
        this.id = UUID.randomUUID();
        this.name = taskName;
        this.syncType = syncType;

        this.stringRepresentation = MoreObjects.toStringHelper(this)
                .add("name", this.name)
                .add("delay", this.offset)
                .add("interval", this.period)
                .add("owner", this.owner)
                .add("id", this.id)
                .add("isAsync", this.isAsynchronous())
                .toString();
    }

    @Override
    public PluginContainer getOwner() {
        return this.owner;
    }

    @Override
    public long getDelay() {
        return this.offset;
    }

    @Override
    public long getInterval() {
        return this.period;
    }

    @Override
    public boolean cancel() {
        boolean success = false;
        if (this.getState() != ScheduledTask.ScheduledTaskState.RUNNING) {
            success = true;
        }
        this.setState(ScheduledTask.ScheduledTaskState.CANCELED);
        return success;
    }

    @Override
    public Consumer<Task> getConsumer() {
        return this.executor;
    }

    @Override
    public UUID getUniqueId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isAsynchronous() {
        return this.syncType == TaskSynchronicity.ASYNCHRONOUS;
    }

    long getTimestamp() {
        return this.timestamp;
    }

    void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    ScheduledTaskState getState() {
        return this.state;
    }

    void setState(ScheduledTaskState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return this.stringRepresentation;
    }

    public enum TaskSynchronicity {
        SYNCHRONOUS,
        ASYNCHRONOUS
    }
}