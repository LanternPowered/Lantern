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

import org.checkerframework.checker.nullness.qual.Nullable;

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
