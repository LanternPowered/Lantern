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

public class LanternTaskBuilder implements TaskBuilder {

    private final LanternScheduler scheduler;

    private Runnable runnable;
    private ScheduledTask.TaskSynchronicity syncType;
    private String name;
    private long delay;
    private long tickDelay;
    private long interval;
    private long tickInterval;

    public LanternTaskBuilder(LanternScheduler scheduler) {
        this.syncType = ScheduledTask.TaskSynchronicity.SYNCHRONOUS;
        this.scheduler = scheduler;
    }

    @Override
    public TaskBuilder async() {
        this.syncType = ScheduledTask.TaskSynchronicity.ASYNCHRONOUS;
        if (this.tickDelay != -1) {
            this.delay = this.tickDelay * LanternGame.TICK_DURATION;
            this.tickDelay = -1;
        }
        if (this.tickInterval != -1) {
            this.interval = this.tickInterval * LanternGame.TICK_DURATION;
            this.tickInterval = -1;
        }
        return this;
    }

    @Override
    public TaskBuilder execute(Runnable runnable) {
        this.runnable = runnable;
        return this;
    }

    @Override
    public TaskBuilder delay(long delay, TimeUnit unit) {
        checkArgument(delay >= 0, "delay cannot be negative");
        this.delay = checkNotNull(unit, "unit").toMillis(delay);
        this.tickDelay = -1;
        return this;
    }

    @Override
    public TaskBuilder delay(long delay) {
        checkArgument(delay >= 0, "delay cannot be negative");
        if (this.syncType == ScheduledTask.TaskSynchronicity.ASYNCHRONOUS) {
            this.delay = delay;
        } else {
            this.tickDelay = delay;
        }
        return this;
    }

    @Override
    public TaskBuilder interval(long interval, TimeUnit unit) {
        checkArgument(interval >= 0, "interval cannot be negative");
        this.interval = checkNotNull(unit, "unit").toMillis(interval);
        this.tickInterval = -1;
        return this;
    }

    @Override
    public TaskBuilder interval(long interval) {
        checkArgument(interval >= 0, "interval cannot be negative");
        if (this.syncType == ScheduledTask.TaskSynchronicity.ASYNCHRONOUS) {
            this.interval = interval;
        } else {
            this.tickInterval = interval;
        }
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
        checkState(this.runnable != null, "runnable task not set");
        String name;
        if (this.name == null) {
            name = this.scheduler.getNameFor(pluginContainer, this.syncType);
        } else {
            name = this.name;
        }
        long delay = this.tickDelay != -1 ? this.tickDelay : this.delay;
        long interval = this.tickInterval != -1 ? this.tickInterval : this.interval;
        ScheduledTask task = new ScheduledTask(this.syncType, this.runnable, name, delay, this.tickDelay != -1,
                interval, this.tickInterval != -1, pluginContainer);
        this.scheduler.submit(task);
        return task;
    }
}