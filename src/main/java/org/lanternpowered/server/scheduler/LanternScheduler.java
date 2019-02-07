package org.lanternpowered.server.scheduler;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkPlugin;

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.util.function.TriFunction;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.scheduler.TaskExecutorService;
import org.spongepowered.api.util.Functional;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class LanternScheduler implements Scheduler {

    private final ScheduledExecutorService executorService;
    private final Map<UUID, LanternScheduledTask> tasksByUniqueId = new ConcurrentHashMap<>();

    public LanternScheduler(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    public void shutdown(long timeout, TimeUnit unit) {
        for (LanternScheduledTask task : this.tasksByUniqueId.values()) {
            task.cancel();
        }
        try {
            this.executorService.shutdown();
            if (!this.executorService.awaitTermination(timeout, unit)) {
                this.executorService.shutdownNow();
            }
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public Optional<ScheduledTask> getTaskById(UUID id) {
        checkNotNull(id, "id");
        return Optional.ofNullable(this.tasksByUniqueId.get(id));
    }

    @Override
    public Set<ScheduledTask> getTasksByName(String pattern) {
        final Pattern searchPattern = Pattern.compile(checkNotNull(pattern, "pattern"));
        return this.tasksByUniqueId.values().stream()
                .filter(task -> searchPattern.matcher(task.getName()).matches())
                .collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public Set<ScheduledTask> getTasks() {
        return ImmutableSet.copyOf(this.tasksByUniqueId.values());
    }

    @Override
    public Set<ScheduledTask> getTasksByPlugin(Object plugin) {
        final PluginContainer pluginContainer = checkPlugin(plugin, "plugin");
        return this.tasksByUniqueId.values().stream()
                .filter(task -> task.getOwner().equals(pluginContainer))
                .collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public TaskExecutorService createExecutor(Object plugin) {
        final PluginContainer pluginContainer = checkPlugin(plugin, "plugin");
        return new LanternTaskExecutorService(() -> new LanternTaskBuilder().plugin(pluginContainer), this);
    }

    /**
     * Removes the stored {@link LanternScheduledTask}.
     *
     * @param task The scheduled task
     */
    void remove(LanternScheduledTask task) {
        this.tasksByUniqueId.remove(task.getUniqueId());
    }

    @SuppressWarnings("unchecked")
    @Override
    public LanternScheduledTask submit(Task task) {
        return submit(task, (executor, scheduledTask, runnable) -> {
            final long delay = scheduledTask.task.delay;
            final long interval = scheduledTask.task.interval;
            if (interval != 0) {
                return executor.scheduleAtFixedRate(runnable, delay, interval, TimeUnit.NANOSECONDS);
            } else if (delay != 0) {
                return executor.schedule(runnable, delay, TimeUnit.NANOSECONDS);
            } else {
                return executor.submit(runnable);
            }
        });
    }

    LanternScheduledTask submit(Task task, TriFunction<ScheduledExecutorService, LanternScheduledTask, Runnable, Future<?>> submitFunction) {
        final LanternScheduledTask scheduledTask = new LanternScheduledTask(task, this);
        final Runnable runnable = () -> {
            final CauseStack causeStack = CauseStack.currentOrEmpty();
            causeStack.pushCause(task.getOwner());
            causeStack.pushCause(task);
            try (CauseStack.Frame ignored = causeStack.pushCauseFrame()) {
                task.getConsumer().accept(scheduledTask);
            } catch (Throwable throwable) {
                task.getOwner().getLogger().error("Error while handling task: {}", task.getName(), throwable);
            }
            causeStack.popCauses(2);
            // Remove the scheduled task once it's done,
            // only do this if it's not a repeated task
            if (scheduledTask.task.interval == 0 || scheduledTask.scheduledRemoval) {
                remove(scheduledTask);
            }
        };
        scheduledTask.setFuture(submitFunction.apply(this.executorService, scheduledTask, runnable));
        this.tasksByUniqueId.put(scheduledTask.getUniqueId(), scheduledTask);
        return scheduledTask;
    }

    public <T> CompletableFuture<T> submit(Callable<T> callable) {
        return Functional.asyncFailableFuture(callable, this.executorService);
    }

    public CompletableFuture<Void> submit(Runnable runnable) {
        return Functional.asyncFailableFuture(() -> {
            runnable.run();
            return null;
        }, this.executorService);
    }
}
