package org.lanternpowered.server.service.scheduler;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.service.scheduler.Task;

public class AsyncScheduler extends SchedulerBase {

    // Adjustable timeout for pending Tasks
    private long minimumTimeout = Long.MAX_VALUE;
    private long lastProcessingTimestamp;
    // Locking mechanism
    private final Lock lock = new ReentrantLock();
    private final Condition condition = this.lock.newCondition();
    // The dynamic thread pooling executor of asynchronous tasks.
    private ExecutorService executor;

    AsyncScheduler() {
        super(ScheduledTask.TaskSynchronicity.ASYNCHRONOUS);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mainLoop();
            }
        });
        thread.setName("Sponge Async Scheduler Thread");
        thread.setDaemon(true);
        thread.start();
    }

    private void mainLoop() {
        this.executor = Executors.newCachedThreadPool();
        this.lastProcessingTimestamp = System.currentTimeMillis();
        while (true) {
            this.recalibrateMinimumTimeout();
            this.runTick();
        }
    }

    private void recalibrateMinimumTimeout() {
        this.lock.lock();
        try {
            Set<Task> tasks = this.getScheduledTasks();
            this.minimumTimeout = Long.MAX_VALUE;
            long now = System.currentTimeMillis();
            for (Task tmpTask : tasks) {
                ScheduledTask task = (ScheduledTask) tmpTask;
                // Recalibrate the wait delay for processing tasks before new
                // tasks cause the scheduler to process pending tasks.
                if (task.offset == 0 && task.period == 0) {
                    this.minimumTimeout = 0;
                }
                // The time since the task last executed or was added to the map
                long timeSinceLast = now - task.getTimestamp();

                if (task.offset > 0 && task.getState() == ScheduledTask.ScheduledTaskState.WAITING) {
                    // There is an offset and the task hasn't run yet
                    this.minimumTimeout = Math.min(task.offset - timeSinceLast, this.minimumTimeout);
                }
                if (task.period > 0 && task.getState().isActive) {
                    // The task repeats and has run after the initial delay
                    this.minimumTimeout = Math.min(task.period - timeSinceLast, this.minimumTimeout);
                }
                if (this.minimumTimeout <= 0) {
                    break;
                }
            }
            if (!tasks.isEmpty()) {
                long latency = System.currentTimeMillis() - this.lastProcessingTimestamp;
                this.minimumTimeout -= (latency <= 0) ? 0 : latency;
                this.minimumTimeout = (this.minimumTimeout < 0) ? 0 : this.minimumTimeout;
            }
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    protected void preTick() {
        this.lock.lock();
        try {
            this.condition.await(this.minimumTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
            // The taskMap has been modified; there is work to do.
            // Continue on without handling the Exception.
        } catch (IllegalMonitorStateException e) {
            LanternGame.log().error("The scheduler internal state machine suffered a catastrophic error", e);
        }
    }

    @Override
    protected void postTick() {
        this.lastProcessingTimestamp = System.currentTimeMillis();
    }

    @Override
    protected void finallyPostTick() {
        this.lock.unlock();
    }

    @Override
    protected void executeTaskRunnable(Runnable runnable) {
        this.executor.submit(runnable);
    }

    @Override
    protected void addTask(ScheduledTask task) {
        this.lock.lock();
        try {
            super.addTask(task);
            this.condition.signalAll();
        } finally {
            this.lock.unlock();
        }
    }

}