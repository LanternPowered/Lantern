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

import org.lanternpowered.api.util.ToStringHelper;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;

import java.util.UUID;
import java.util.concurrent.Future;

import javax.annotation.Nullable;

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
