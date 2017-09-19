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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

abstract class SchedulerBase {

    // The simple queue of all pending (and running) ScheduledTasks
    private final Map<UUID, ScheduledTask> taskMap = Maps.newConcurrentMap();
    private long sequenceNumber = 0L;
    private final String taskNameFmt;

    protected SchedulerBase(ScheduledTask.TaskSynchronicity type) {
        this.taskNameFmt = "%s-" + (type == ScheduledTask.TaskSynchronicity.SYNCHRONOUS ? "S" : "A") + "-%d";
    }

    protected String nextName(PluginContainer plugin) {
        return String.format(this.taskNameFmt, plugin.getId(), this.sequenceNumber++);
    }

    /**
     * Gets the timestamp to update the timestamp of a task. This method is task
     * sensitive to support different timestamp types i.e. real time and ticks.
     *
     * <p>Subtracting the result of this method from a previously obtained
     * result should become a representation of the time that has passed
     * between those calls.</p>
     *
     * @param task The task
     * @return Timestamp for the task
     */
    protected long getTimestamp(ScheduledTask task) {
        // Supports wall clock time by default
        return System.nanoTime();
    }

    /**
     * Adds the task to the task map, will attempt to process the task on the
     * next call to {@link #runTick}.
     *
     * @param task The task to add
     */
    protected void addTask(ScheduledTask task) {
        task.setTimestamp(this.getTimestamp(task));
        this.taskMap.put(task.getUniqueId(), task);
    }

    /**
     * Removes the task from the task map.
     *
     * @param task The task to remove
     */
    protected void removeTask(ScheduledTask task) {
        this.taskMap.remove(task.getUniqueId());
    }

    protected Optional<Task> getTask(UUID id) {
        return Optional.ofNullable(this.taskMap.get(id));
    }

    protected Set<Task> getScheduledTasks() {
        synchronized (this.taskMap) {
            return new HashSet<>(this.taskMap.values());
        }
    }

    /**
     * Process all tasks in the map.
     */
    protected final void runTick() {
        preTick();
        try {
            this.taskMap.values().forEach(this::processTask);
            postTick();
        } finally {
            finallyPostTick();
        }
    }

    /**
     * Fired when the scheduler begins to tick, before any tasks are processed.
     */
    protected void preTick() {
    }

    /**
     * Fired when the scheduler has processed all tasks.
     */
    protected void postTick() {
    }

    /**
     * Fired after tasks have attempted to be processed, in a finally block to
     * guarantee execution regardless of any error when processing a task.
     */
    protected void finallyPostTick() {
    }

    /**
     * Processes the task.
     *
     * @param task The task to process
     */
    protected void processTask(ScheduledTask task) {
        // If the task is now slated to be cancelled, we just remove it as if it
        // no longer exists.
        if (task.getState() == ScheduledTask.ScheduledTaskState.CANCELED) {
            removeTask(task);
            return;
        }
        long threshold = Long.MAX_VALUE;
        // Figure out if we start a delayed Task after threshold ticks or, start
        // it after the interval (period) of the repeating task parameter.
        if (task.getState() == ScheduledTask.ScheduledTaskState.WAITING) {
            threshold = task.offset;
        } else if (task.getState() == ScheduledTask.ScheduledTaskState.RUNNING) {
            threshold = task.period;
        }
        // This moment is 'now'
        long now = getTimestamp(task);
        // So, if the current time minus the timestamp of the task is greater
        // than the delay to wait before starting the task, then start the task.
        // Repeating tasks get a reset-timestamp each time they are set RUNNING
        // If the task has a period of 0 (zero) this task will not repeat, and
        // is removed after we start it.
        if (threshold <= (now - task.getTimestamp())) {
            task.setState(ScheduledTask.ScheduledTaskState.SWITCHING);
            task.setTimestamp(getTimestamp(task));
            startTask(task);
            // If task is one time shot, remove it from the map.
            if (task.period == 0L) {
                removeTask(task);
            }
        }
    }

    /**
     * Begin the execution of a task. Exceptions are caught and logged.
     *
     * @param task The task to start
     */
    protected void startTask(final ScheduledTask task) {
        executeTaskRunnable(task, () -> {
            task.setState(ScheduledTask.ScheduledTaskState.RUNNING);
            try {
                task.getConsumer().accept(task);
            } catch (Throwable t) {
                Lantern.getLogger().error("The Scheduler tried to run the task {} owned by {}, but an error occurred.",
                        task.getName(), task.getOwner(), t);
            }
        });
    }

    /**
     * Actually run the runnable of a task.
     *
     * @param runnable The runnable to run
     */
    protected abstract void executeTaskRunnable(ScheduledTask task, Runnable runnable);

}
