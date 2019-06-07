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
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * An internal representation of a {@link Task} created by a plugin.
 */
public class LanternTask implements Task {

    final long delay; // nanos
    final long interval; // nanos
    private final PluginContainer owner;
    private final Consumer<ScheduledTask> executor;
    private final String name;
    @Nullable private String toString;

    // The amount of times this task has been scheduled
    final AtomicInteger scheduledCounter = new AtomicInteger();

    LanternTask(Consumer<ScheduledTask> executor, String taskName, PluginContainer pluginContainer, long delay, long interval) {
        this.delay = delay;
        this.interval = interval;
        this.owner = pluginContainer;
        this.executor = executor;
        this.name = taskName;
    }

    @Override
    public PluginContainer getOwner() {
        return this.owner;
    }

    @Override
    public Duration getDelay() {
        return Duration.ofNanos(this.delay);
    }

    @Override
    public Duration getInterval() {
        return Duration.ofNanos(this.interval);
    }

    @Override
    public Consumer<ScheduledTask> getConsumer() {
        return this.executor;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        String toString = this.toString;
        if (toString == null) {
            toString = this.toString = new ToStringHelper("Task")
                    .add("name", this.name)
                    .add("delay", this.delay)
                    .add("interval", this.interval)
                    .add("owner", this.owner)
                    .toString();
        }
        return toString;
    }
}
