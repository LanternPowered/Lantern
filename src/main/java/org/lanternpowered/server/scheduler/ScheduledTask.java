/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.scheduler;

import co.aikar.timings.LanternTimings;
import co.aikar.timings.Timing;
import com.google.common.base.MoreObjects;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.annotation.Nullable;

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
    final TaskSynchronicity syncType;
    private final String stringRepresentation;
    @Nullable private Timing taskTimer;

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

        ScheduledTaskState(boolean active) {
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
        if (this.delayIsTicks) {
            return this.offset;
        } else {
            return TimeUnit.NANOSECONDS.toMillis(this.offset);
        }
    }

    @Override
    public long getInterval() {
        if (this.intervalIsTicks) {
            return this.period;
        } else {
            return TimeUnit.NANOSECONDS.toMillis(this.period);
        }
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

    /**
     * Returns a timestamp after which the next execution will take place.
     * Should only be compared to
     * {@link SchedulerBase#getTimestamp(ScheduledTask)}.
     *
     * @return The next execution timestamp
    */
    long nextExecutionTimestamp() {
        if (this.state.isActive) {
            return this.timestamp + this.period;
        } else {
            return this.timestamp + this.offset;
        }
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

    public Timing getTimingsHandler() {
        if (this.taskTimer == null) {
            this.taskTimer = LanternTimings.getPluginSchedulerTimings(this.owner);
        }
        return this.taskTimer;
    }

    public enum TaskSynchronicity {
        SYNCHRONOUS,
        ASYNCHRONOUS
    }
}
