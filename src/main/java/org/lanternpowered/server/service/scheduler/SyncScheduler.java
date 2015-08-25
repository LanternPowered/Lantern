package org.lanternpowered.server.service.scheduler;

public class SyncScheduler extends SchedulerBase {

    // The number of ticks elapsed since this scheduler began.
    private volatile long counter = 0L;

    SyncScheduler() {
        super(ScheduledTask.TaskSynchronicity.SYNCHRONOUS);
    }

    /**
     * The hook to update the Ticks known by the SyncScheduler.
     */
    void tick() {
        this.counter++;
        this.runTick();
    }

    @Override
    protected long getTimestamp(ScheduledTask task) {
        if (task.getState() == ScheduledTask.ScheduledTaskState.WAITING) {
            // The timestamp is based on the initial offset
            if (task.delayIsTicks) {
                return this.counter;
            } else {
                return super.getTimestamp(task);
            }
        } else if (task.getState().isActive) {
            // The timestamp is based on the period
            if (task.intervalIsTicks) {
                return this.counter;
            } else {
                return super.getTimestamp(task);
            }
        }
        return 0L;
    }

    @Override
    protected void executeTaskRunnable(Runnable runnable) {
        runnable.run();
    }

}