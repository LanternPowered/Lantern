package org.lanternpowered.server.service.scheduler;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkPlugin;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.scheduler.SchedulerService;
import org.spongepowered.api.service.scheduler.Task;
import org.spongepowered.api.service.scheduler.TaskBuilder;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ListenableFutureTask;

public class LanternScheduler implements SchedulerService {

    private static final LanternScheduler INSTANCE = new LanternScheduler();

    /**
     * Gets the {@link LanternScheduler} instance.
     * 
     * @return the scheduler
     */
    public static LanternScheduler getInstance() {
        return INSTANCE;
    }

    private final AsyncScheduler asyncScheduler = new AsyncScheduler();
    private final SyncScheduler syncScheduler = new SyncScheduler();

    @Override
    public TaskBuilder createTaskBuilder() {
        return new LanternTaskBuilder(this);
    }

    @Override
    public Optional<Task> getTaskById(UUID id) {
        Optional<Task> task = this.syncScheduler.getTask(id);
        if (task.isPresent()) {
            return task;
        }
        return this.asyncScheduler.getTask(id);
    }

    @Override
    public Set<Task> getTasksByName(String pattern) {
        Pattern searchPattern = Pattern.compile(checkNotNull(pattern, "pattern"));
        Set<Task> matchingTasks = this.getScheduledTasks();

        Iterator<Task> it = matchingTasks.iterator();
        while (it.hasNext()) {
            Matcher matcher = searchPattern.matcher(it.next().getName());
            if (!matcher.matches()) {
                it.remove();
            }
        }

        return matchingTasks;
    }

    @Override
    public Set<Task> getScheduledTasks() {
        ImmutableSet.Builder<Task> builder = ImmutableSet.builder();
        builder.addAll(this.asyncScheduler.getScheduledTasks());
        builder.addAll(this.syncScheduler.getScheduledTasks());
        return builder.build();
    }

    @Override
    public Set<Task> getScheduledTasks(boolean async) {
        if (async) {
            return this.asyncScheduler.getScheduledTasks();
        } else {
            return this.syncScheduler.getScheduledTasks();
        }
    }

    @Override
    public Set<Task> getScheduledTasks(Object plugin) {
        String testOwnerId = checkPlugin(plugin, "plugin").getId();

        Set<Task> allTasks = this.getScheduledTasks();
        Iterator<Task> it = allTasks.iterator();

        while (it.hasNext()) {
            String taskOwnerId = it.next().getOwner().getId();
            if (!testOwnerId.equals(taskOwnerId)) {
                it.remove();
            }
        }

        return allTasks;
    }

    @Override
    public int getPreferredTickInterval() {
        return LanternGame.TICK_DURATION;
    }

    /**
     * Calls the callable from the main thread.
     * 
     * @param callable the callable
     * @return the future result
     */
    public <V> Future<V> callSync(Callable<V> callable) {
        ListenableFutureTask<V> future = ListenableFutureTask.create(callable);
        this.createTaskBuilder().execute(future).submit(LanternGame.plugin());
        return future;
    }

    private SchedulerBase getDelegate(Task task) {
        if (task.isAsynchronous()) {
            return this.asyncScheduler;
        } else {
            return this.syncScheduler;
        }
    }

    private SchedulerBase getDelegate(ScheduledTask.TaskSynchronicity syncType) {
        if (syncType == ScheduledTask.TaskSynchronicity.ASYNCHRONOUS) {
            return this.asyncScheduler;
        } else {
            return this.syncScheduler;
        }
    }

    String getNameFor(PluginContainer plugin, ScheduledTask.TaskSynchronicity syncType) {
        return this.getDelegate(syncType).nextName(plugin);
    }

    void submit(ScheduledTask task) {
        this.getDelegate(task).addTask(task);
    }

    /**
     * Pulses the synchronous scheduler.
     */
    public void pulseSyncScheduler() {
        this.syncScheduler.tick();
    }

    public void shutdownAsyncScheduler() {
        this.asyncScheduler.shutdown();
    }
}