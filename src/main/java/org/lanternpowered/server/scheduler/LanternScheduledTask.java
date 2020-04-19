/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.scheduler;

import org.lanternpowered.api.util.ToStringHelper;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;

import java.util.UUID;
import java.util.concurrent.Future;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * An internal representation of a {@link Task} created by a plugin.
 */
public class LanternScheduledTask implements ScheduledTask {

    private final UUID uniqueId;
    private final String name;
    final LanternTask task;
    private final LanternScheduler scheduler;

    @Nullable private Future<?> future;
    private final Object futureLock = new Object();

    volatile boolean scheduledRemoval;

    LanternScheduledTask(Task task, LanternScheduler scheduler) {
        this.scheduler = scheduler;
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
        return cancel(false);
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        final Future<?> future = getFuture();
        if (future.isDone()) {
            return false;
        }
        // If the task is successfully cancelled, just
        // remove it from the scheduler
        if (future.cancel(mayInterruptIfRunning)) {
            this.scheduler.remove(this);
        } else {
            this.scheduledRemoval = true;
        }
        return false;
    }

    void setFuture(Future<?> future) {
        synchronized (this.futureLock) {
            this.future = future;
            this.futureLock.notifyAll();
        }
    }

    Future<?> getFuture() {
        synchronized (this.futureLock) {
            while (this.future == null) {
                try {
                    this.futureLock.wait();
                } catch (InterruptedException ignored) {
                }
            }
        }
        return this.future;
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
