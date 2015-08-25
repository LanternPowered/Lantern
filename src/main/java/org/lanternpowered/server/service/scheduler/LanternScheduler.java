package org.lanternpowered.server.service.scheduler;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkPlugin;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.scheduler.SchedulerService;
import org.spongepowered.api.service.scheduler.Task;
import org.spongepowered.api.service.scheduler.TaskBuilder;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

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
        return this.syncScheduler.getTask(checkNotNull(id, "id")).or(this.asyncScheduler.getTask(id));
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
     * Ticks the synchronous scheduler.
     */
    public void tickSyncScheduler() {
        this.syncScheduler.tick();
    }

}