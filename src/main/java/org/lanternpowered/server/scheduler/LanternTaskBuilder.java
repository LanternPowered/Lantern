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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import org.lanternpowered.api.cause.CauseStack;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import javax.annotation.Nullable;

public class LanternTaskBuilder implements Task.Builder {

    private static AtomicInteger taskCounter = new AtomicInteger();

    @Nullable private Consumer<ScheduledTask> consumer;
    @Nullable private String name;
    private Duration delay;
    private Duration interval;
    @Nullable private PluginContainer plugin;

    public LanternTaskBuilder() {
        reset();
    }

    @Override
    public LanternTaskBuilder reset() {
        this.name = null;
        this.consumer = null;
        this.delay = Duration.ZERO;
        this.interval = Duration.ZERO;
        return this;
    }

    @Override
    public LanternTaskBuilder execute(Consumer<ScheduledTask> consumer) {
        this.consumer = consumer;
        return this;
    }

    @Override
    public LanternTaskBuilder delay(Duration delay) {
        this.delay = checkNotNull(delay, "delay");
        return this;
    }

    @Override
    public LanternTaskBuilder interval(Duration interval) {
        this.interval = checkNotNull(interval, "interval");
        return this;
    }

    @Override
    public LanternTaskBuilder name(String name) {
        this.name = checkNotNullOrEmpty(name, "name");
        return this;
    }

    @Override
    public LanternTaskBuilder plugin(PluginContainer plugin) {
        this.plugin = checkNotNull(plugin, "plugin");
        return this;
    }

    @Override
    public Task build() {
        checkState(this.consumer != null, "consumer not set");
        final String name;
        PluginContainer plugin = this.plugin;
        if (plugin == null) {
            plugin = CauseStack.currentOrEmpty().first(PluginContainer.class)
                    .orElseThrow(() -> new IllegalStateException("No PluginContainer found in the CauseStack."));
        }
        if (this.name == null) {
            final int number = taskCounter.incrementAndGet();
            name = String.format("%s-%s", plugin.getId(), number);
        } else {
            name = this.name;
        }
        final long delay = this.delay.toNanos();
        final long interval = this.interval.toNanos();
        return new LanternTask(this.consumer, name, plugin, delay, interval);
    }

    @Override
    public LanternTaskBuilder from(Task value) {
        this.reset();
        final LanternTask task = (LanternTask) value;
        this.delay = task.getDelay();
        this.interval = task.getInterval();
        this.consumer = task.getConsumer();
        this.name = task.getName();
        return this;
    }
}
