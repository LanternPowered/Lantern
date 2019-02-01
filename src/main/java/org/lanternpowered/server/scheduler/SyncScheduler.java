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

import org.lanternpowered.api.cause.CauseStack;

final class SyncScheduler extends SchedulerBase {

    SyncScheduler() {
    }

    /**
     * The hook to update the Ticks known by the SyncScheduler.
     */
    void tick() {
        runTick();
    }

    @Override
    protected long getTimestamp(LanternScheduledTask task) {
        // The timestamp is based on the initial delay
        if (task.getState() == LanternScheduledTask.ScheduledTaskState.WAITING ||
                // The timestamp is based on the interval
                task.getState().isActive) {
            return super.getTimestamp(task);
        }
        return 0L;
    }

    @Override
    protected void executeTaskRunnable(LanternScheduledTask task, Runnable runnable) {
        final CauseStack causeStack = CauseStack.current();
        causeStack.pushCause(task.getOwner());
        causeStack.pushCause(task);
        runnable.run();
        causeStack.popCauses(2);
    }
}
