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

import org.lanternpowered.server.util.ToStringHelper;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;

import java.util.UUID;

/**
 * An internal representation of a {@link Task} created by a plugin.
 */
public class LanternScheduledTask implements ScheduledTask {

    private final UUID uniqueId;
    final LanternTask task;
    private final String name;
    private long timestamp;
    private ScheduledTaskState state = ScheduledTaskState.WAITING;

    // Internal Task state. Not for user-service use.
    public enum ScheduledTaskState {
        /**
         * Never ran before, waiting for the delay to pass.
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

    LanternScheduledTask(Task task) {
        this.uniqueId = UUID.randomUUID();
        this.task = (LanternTask) task;
        this.name = task.getName() + "-" + this.task.scheduledCounter.incrementAndGet();
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Task getTask() {
        return this.task;
    }

    @Override
    public boolean cancel() {
        boolean success = false;
        if (this.getState() != LanternScheduledTask.ScheduledTaskState.RUNNING) {
            success = true;
        }
        this.setState(LanternScheduledTask.ScheduledTaskState.CANCELED);
        return success;
    }

    /**
     * Returns a timestamp after which the next execution will take place.
     * Should only be compared to
     * {@link SchedulerBase#getTimestamp(LanternScheduledTask)}.
     *
     * @return The next execution timestamp
    */
    long nextExecutionTimestamp() {
        if (this.state.isActive) {
            return this.timestamp + this.task.interval;
        } else {
            return this.timestamp + this.task.delay;
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
        return new ToStringHelper("ScheduledTask")
                .add("uniqueId", this.uniqueId)
                .add("name", this.name)
                .add("task", this.task)
                .toString();
    }
}
