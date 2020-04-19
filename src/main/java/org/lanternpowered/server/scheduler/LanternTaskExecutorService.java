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

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.ScheduledTaskFuture;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.scheduler.TaskExecutorService;
import org.spongepowered.api.scheduler.TaskFuture;

import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("unchecked")
class LanternTaskExecutorService extends AbstractExecutorService implements TaskExecutorService {

    private final Supplier<Task.Builder> taskBuilderProvider;
    private final LanternScheduler scheduler;

    LanternTaskExecutorService(Supplier<Task.Builder> taskBuilderProvider, LanternScheduler scheduler) {
        this.taskBuilderProvider = taskBuilderProvider;
        this.scheduler = scheduler;
    }

    @Override
    public void shutdown() {
        // Since this class is delegating its work to SchedulerService
        // and we have no way to stopping execution without keeping
        // track of all the submitted tasks, it makes sense that
        // this ExecutionService cannot be shut down.

        // While it is technically possible to cancel all tasks for
        // a plugin through the SchedulerService, we have no way to
        // ensure those tasks were created through this interface.
    }

    @Override
    public List<Runnable> shutdownNow() {
        return ImmutableList.of();
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) {
        return false;
    }

    @Override
    public void execute(Runnable command) {
        this.scheduler.submit(createTask(command).build());
    }

    @Override
    public TaskFuture<?> submit(Runnable command) {
        return submit(command, null);
    }

    @Override
    public <T> TaskFuture<T> submit(Callable<T> command) {
        final FutureTask<T> runnable = new FutureTask<>(command);
        final Task task = createTask(runnable).build();
        return new LanternTaskFuture<>(this.scheduler.submit(task), runnable);
    }

    @Override
    public <T> TaskFuture<T> submit(Runnable command, @Nullable T result) {
        final FutureTask<T> runnable = new FutureTask<>(command, result);
        final Task task = createTask(runnable).build();
        return new LanternTaskFuture<>(this.scheduler.submit(task), runnable);
    }

    private LanternScheduledTask submitScheduledTask(Task task) {
        return this.scheduler.submit(task, (executor, scheduledTask, runnable) -> {
            final long delay = scheduledTask.task.delay;
            final long interval = scheduledTask.task.interval;
            if (interval != 0) {
                return executor.scheduleAtFixedRate(runnable, delay, interval, TimeUnit.NANOSECONDS);
            } else {
                return executor.schedule(runnable, delay, TimeUnit.NANOSECONDS);
            }
        });
    }

    @Override
    public ScheduledTaskFuture<?> schedule(Runnable command, long delay, TemporalUnit unit) {
        final Task task = createTask(command).delay(delay, unit).build();
        return new LanternScheduledTaskFuture<>(submitScheduledTask(task));
    }

    @Override
    public ScheduledTaskFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        final Task task = createTask(command).delay(delay, unit).build();
        return new LanternScheduledTaskFuture<>(submitScheduledTask(task));
    }

    @Override
    public <V> ScheduledTaskFuture<V> schedule(Callable<V> callable, long delay, TemporalUnit unit) {
        final FutureTask<V> runnable = new FutureTask<>(callable);
        final Task task = createTask(runnable).delay(delay, unit).build();
        return new LanternScheduledTaskFuture<>(submitScheduledTask(task), runnable);
    }

    @Override
    public <V> ScheduledTaskFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        final FutureTask<V> runnable = new FutureTask<>(callable);
        final Task task = createTask(runnable).delay(delay, unit).build();
        return new LanternScheduledTaskFuture<>(submitScheduledTask(task), runnable);
    }

    @Override
    public ScheduledTaskFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TemporalUnit unit) {
        final Task task = createTask(command).delay(initialDelay, unit).interval(period, unit).build();
        return new LanternScheduledTaskFuture<>(submitScheduledTask(task));
    }

    @Override
    public ScheduledTaskFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        final Task task = createTask(command).delay(initialDelay, unit).interval(period, unit).build();
        return new LanternScheduledTaskFuture<>(submitScheduledTask(task));
    }

    private LanternScheduledTask submitTaskWithFixedDelay(Task task) {
        return this.scheduler.submit(task, (executor, scheduledTask, runnable) -> {
            final long delay = scheduledTask.task.delay;
            final long interval = scheduledTask.task.interval;
            return executor.scheduleWithFixedDelay(runnable, delay, interval, TimeUnit.NANOSECONDS);
        });
    }

    @Override
    public ScheduledTaskFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TemporalUnit unit) {
        final Task task = createTask(command).delay(initialDelay, unit).interval(delay, unit).build();
        return new LanternScheduledTaskFuture<>(submitTaskWithFixedDelay(task));
    }

    @Override
    public ScheduledTaskFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        final Task task = createTask(command).delay(initialDelay, unit).interval(delay, unit).build();
        return new LanternScheduledTaskFuture<>(submitTaskWithFixedDelay(task));
    }

    private Task.Builder createTask(Runnable command) {
        return this.taskBuilderProvider.get()
                .execute(command);
    }

    private static class LanternTaskFuture<V, F extends Future<?>> implements TaskFuture<V> {

        final LanternScheduledTask task;
        final Future<V> resultFuture;
        final F future;

        LanternTaskFuture(LanternScheduledTask task, Future<V> resultFuture) {
            this.future = (F) task.getFuture();
            this.resultFuture = resultFuture;
            this.task = task;
        }

        @Override
        public ScheduledTask getTask() {
            return this.task;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return this.task.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean isCancelled() {
            return this.future.isCancelled();
        }

        @Override
        public boolean isDone() {
            return this.future.isDone();
        }

        @Override
        public V get() throws InterruptedException, ExecutionException {
            return this.resultFuture.get();
        }

        @Override
        public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return this.resultFuture.get(timeout, unit);
        }
    }

    private static class LanternScheduledTaskFuture<V> extends LanternTaskFuture<V, ScheduledTaskFuture<?>> implements ScheduledTaskFuture<V> {

        LanternScheduledTaskFuture(LanternScheduledTask task, Future<V> resultFuture) {
            super(task, resultFuture);
        }

        LanternScheduledTaskFuture(LanternScheduledTask task) {
            super(task, (Future<V>) task.getFuture());
        }

        @Override
        public boolean isPeriodic() {
            return this.future.isPeriodic();
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return this.future.getDelay(unit);
        }

        @Override
        public int compareTo(Delayed o) {
            return this.future.compareTo(o);
        }

        @Override
        public void run() {
            this.future.run();
        }
    }
}
