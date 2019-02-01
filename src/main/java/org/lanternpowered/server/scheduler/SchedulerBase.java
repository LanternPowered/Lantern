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

import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

abstract class SchedulerBase {

    // The simple queue of all pending (and running) ScheduledTasks
    private final Map<UUID, LanternScheduledTask> taskMap = new ConcurrentHashMap<>();

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
    protected long getTimestamp(LanternScheduledTask task) {
        // Supports wall clock time by default
        return System.nanoTime();
    }

    protected LanternScheduledTask submit(Task task) {
        final LanternScheduledTask scheduledTask = new LanternScheduledTask(task);
        addTask(scheduledTask);
        return scheduledTask;
    }

    /**
     * Adds the task to the task map, will attempt to process the task on the
     * next call to {@link #runTick}.
     *
     * @param task The task to add
     */
    protected void addTask(LanternScheduledTask task) {
        task.setTimestamp(this.getTimestamp(task));
        this.taskMap.put(task.getUniqueId(), task);
    }

    /**
     * Removes the task from the task map.
     *
     * @param task The task to remove
     */
    protected void removeTask(LanternScheduledTask task) {
        this.taskMap.remove(task.getUniqueId());
    }

    protected Optional<ScheduledTask> getTask(UUID id) {
        return Optional.ofNullable(this.taskMap.get(id));
    }

    protected Set<ScheduledTask> getScheduledTasks() {
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
     * Processes the scheduled task.
     *
     * @param scheduled The scheduled task to process
     */
    protected void processTask(LanternScheduledTask scheduled) {
        // If the task is now slated to be cancelled, we just remove it as if it
        // no longer exists.
        if (scheduled.getState() == LanternScheduledTask.ScheduledTaskState.CANCELED) {
            removeTask(scheduled);
            return;
        }
        long threshold = Long.MAX_VALUE;
        // Figure out if we start a delayed Task after threshold ticks or, start
        // it after the interval (interval) of the repeating task parameter.
        if (scheduled.getState() == LanternScheduledTask.ScheduledTaskState.WAITING) {
            threshold = scheduled.task.delay;
        } else if (scheduled.getState() == LanternScheduledTask.ScheduledTaskState.RUNNING) {
            threshold = scheduled.task.interval;
        }
        // This moment is 'now'
        long now = getTimestamp(scheduled);
        // So, if the current time minus the timestamp of the task is greater
        // than the delay to wait before starting the task, then start the task.
        // Repeating tasks get a reset-timestamp each time they are set RUNNING
        // If the task has a interval of 0 (zero) this task will not repeat, and
        // is removed after we start it.
        if (threshold <= (now - scheduled.getTimestamp())) {
            scheduled.setState(LanternScheduledTask.ScheduledTaskState.SWITCHING);
            scheduled.setTimestamp(getTimestamp(scheduled));
            startTask(scheduled);
            // If task is one time shot, remove it from the map.
            if (scheduled.task.interval == 0L) {
                removeTask(scheduled);
            }
        }
    }

    /**
     * Begin the execution of a scheduled task. Exceptions are caught and logged.
     *
     * @param scheduled The scheduled task to start
     */
    protected void startTask(final LanternScheduledTask scheduled) {
        executeTaskRunnable(scheduled, () -> {
            scheduled.setState(LanternScheduledTask.ScheduledTaskState.RUNNING);
            try {
                scheduled.task.getConsumer().accept(scheduled);
            } catch (Throwable t) {
                Lantern.getLogger().error("The Scheduler tried to run the task {} owned by {}, but an error occurred.",
                        scheduled.getName(), scheduled.task.getOwner(), t);
            }
        });
    }

    /**
     * Actually run the runnable of a task.
     *
     * @param runnable The runnable to run
     */
    protected abstract void executeTaskRunnable(LanternScheduledTask task, Runnable runnable);

}
