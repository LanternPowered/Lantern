/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.service.scheduler;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.Conditions.checkPlugin;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.scheduler.Task;
import org.spongepowered.api.service.scheduler.TaskBuilder;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class LanternTaskBuilder implements TaskBuilder {

    private final LanternScheduler scheduler;

    private Consumer<Task> consumer;
    private ScheduledTask.TaskSynchronicity syncType;
    private String name;
    private long delay;
    private long interval;
    private boolean delayIsTicks;
    private boolean intervalIsTicks;

    public LanternTaskBuilder(LanternScheduler scheduler) {
        this.syncType = ScheduledTask.TaskSynchronicity.SYNCHRONOUS;
        this.scheduler = scheduler;
    }

    @Override
    public TaskBuilder async() {
        this.syncType = ScheduledTask.TaskSynchronicity.ASYNCHRONOUS;
        return this;
    }

    @Override
    public TaskBuilder execute(Consumer<Task> consumer) {
        this.consumer = consumer;
        return null;
    }

    @Override
    public TaskBuilder delay(long delay, TimeUnit unit) {
        checkArgument(delay >= 0, "delay cannot be negative");
        this.delay = checkNotNull(unit, "unit").toMillis(delay);
        this.delayIsTicks = false;
        return this;
    }

    @Override
    public TaskBuilder delayTicks(long delay) {
        checkArgument(delay >= 0, "delay cannot be negative");
        this.delay = delay;
        this.delayIsTicks = true;
        return this;
    }

    @Override
    public TaskBuilder interval(long interval, TimeUnit unit) {
        checkArgument(interval >= 0, "interval cannot be negative");
        this.interval = checkNotNull(unit, "unit").toMillis(interval);
        this.intervalIsTicks = false;
        return this;
    }

    @Override
    public TaskBuilder intervalTicks(long interval) {
        checkArgument(interval >= 0, "interval cannot be negative");
        this.interval = interval;
        this.intervalIsTicks = true;
        return this;
    }

    @Override
    public TaskBuilder name(String name) {
        this.name = checkNotNullOrEmpty(name, "name");
        return this;
    }

    @Override
    public Task submit(Object plugin) {
        PluginContainer pluginContainer = checkPlugin(plugin, "plugin");
        checkState(this.consumer != null, "consumer not set");
        String name;
        if (this.name == null) {
            name = this.scheduler.getNameFor(pluginContainer, this.syncType);
        } else {
            name = this.name;
        }
        long delay = this.delay;
        long interval = this.interval;
        boolean delayIsTicks = this.delayIsTicks;
        boolean intervalIsTicks = this.intervalIsTicks;
        if (this.syncType == ScheduledTask.TaskSynchronicity.ASYNCHRONOUS) {
            delay = delayIsTicks ? delay * LanternGame.TICK_DURATION : delay;
            interval = intervalIsTicks ? interval * LanternGame.TICK_DURATION : interval;
            delayIsTicks = intervalIsTicks = false;
        }
        ScheduledTask task = new ScheduledTask(this.syncType, this.consumer, name, delay,
                delayIsTicks, interval, intervalIsTicks, pluginContainer);
        this.scheduler.submit(task);
        return task;
    }
}