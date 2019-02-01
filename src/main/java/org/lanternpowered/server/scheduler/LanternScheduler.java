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
import static org.lanternpowered.server.util.Conditions.checkPlugin;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.TaskExecutorService;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.scheduler.TaskSynchronicity;
import org.spongepowered.api.util.Functional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Singleton
public class LanternScheduler implements Scheduler {

    private final AsyncScheduler asyncScheduler = new AsyncScheduler();
    private final SyncScheduler syncScheduler = new SyncScheduler();

    @Inject
    public LanternScheduler() {
    }

    @Override
    public Optional<ScheduledTask> getTaskById(UUID id) {
        final Optional<ScheduledTask> task = this.syncScheduler.getTask(id);
        if (task.isPresent()) {
            return task;
        }
        return this.asyncScheduler.getTask(id);
    }

    @Override
    public Set<ScheduledTask> getTasksByName(String pattern) {
        final Pattern searchPattern = Pattern.compile(checkNotNull(pattern, "pattern"));
        return getTasks().stream()
                .filter(task -> searchPattern.matcher(task.getName()).matches())
                .collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public Set<ScheduledTask> getTasks() {
        final ImmutableSet.Builder<ScheduledTask> builder = ImmutableSet.builder();
        builder.addAll(this.asyncScheduler.getScheduledTasks());
        builder.addAll(this.syncScheduler.getScheduledTasks());
        return builder.build();
    }

    @Override
    public Set<ScheduledTask> getTasks(TaskSynchronicity synchronicity) {
        checkNotNull(synchronicity, "synchronicity");
        if (synchronicity == TaskSynchronicity.ASYNC) {
            return this.asyncScheduler.getScheduledTasks();
        } else {
            return this.syncScheduler.getScheduledTasks();
        }
    }

    @Override
    public Set<ScheduledTask> getTasksByPlugin(Object plugin) {
        final PluginContainer pluginContainer = checkPlugin(plugin, "plugin");
        return getTasks().stream()
                .filter(task -> task.getTask().getOwner().equals(pluginContainer))
                .collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public int getPreferredTickInterval() {
        return LanternGame.TICK_DURATION;
    }

    /**
     * Calls the callable from the main thread.
     * 
     * @param callable The callable
     * @return The future result
     */
    public <V> Future<V> callSync(Callable<V> callable) {
        final ListenableFutureTask<V> future = ListenableFutureTask.create(callable);
        this.syncScheduler.addTask((LanternScheduledTask) new LanternTaskBuilder().execute(future).build());
        return future;
    }

    /**
     * Calls the callable from the main thread.
     *
     * @param runnable The runnable
     */
    public void callSync(Runnable runnable) {
        this.syncScheduler.addTask((LanternScheduledTask) new LanternTaskBuilder().execute(runnable).build());
    }

    private SchedulerBase getDelegate(Task task) {
        if (task.getSynchronicity() == TaskSynchronicity.ASYNC) {
            return this.asyncScheduler;
        } else {
            return this.syncScheduler;
        }
    }

    /**
     * Pulses the synchronous scheduler.
     */
    public void pulseSyncScheduler() {
        this.syncScheduler.tick();
    }

    public void shutdownAsyncScheduler(long timeout, TimeUnit unit) {
        this.asyncScheduler.shutdown(timeout, unit);
    }

    @Override
    public TaskExecutorService createSyncExecutor(Object plugin) {
        final PluginContainer pluginContainer = checkPlugin(plugin, "plugin");
        return new org.lanternpowered.server.scheduler.TaskExecutorService(() -> new LanternTaskBuilder().plugin(pluginContainer), this.syncScheduler);
    }

    @Override
    public TaskExecutorService createAsyncExecutor(Object plugin) {
        final PluginContainer pluginContainer = checkPlugin(plugin, "plugin");
        return new org.lanternpowered.server.scheduler.TaskExecutorService(() -> new LanternTaskBuilder().plugin(pluginContainer).async(), this.asyncScheduler);
    }

    @Override
    public ScheduledTask submit(Task task) {
        checkNotNull(task, "task");
        return getDelegate(task).submit(task);
    }

    public <T> CompletableFuture<T> submitAsyncTask(Callable<T> callable) {
        return Functional.asyncFailableFuture(callable, this.asyncScheduler.getExecutor());
    }

    public CompletableFuture<Void> submitAsyncTask(Runnable callable) {
        return Functional.asyncFailableFuture(() -> {
            callable.run();
            return null;
        }, this.asyncScheduler.getExecutor());
    }
}
