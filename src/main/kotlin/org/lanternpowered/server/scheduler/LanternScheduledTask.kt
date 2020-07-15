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
package org.lanternpowered.server.scheduler

import org.lanternpowered.api.util.ToStringHelper
import org.spongepowered.api.scheduler.ScheduledTask
import org.spongepowered.api.scheduler.Task
import java.util.UUID
import java.util.concurrent.Future

/**
 * An internal representation of a [Task] created by a plugin.
 */
class LanternScheduledTask internal constructor(
        private val task: LanternTask,
        private val scheduler: LanternScheduler
) : ScheduledTask {

    private val uniqueId: UUID = UUID.randomUUID()
    private val name: String = this.task.name + "-" + this.task.scheduledCounter.incrementAndGet()
    private val futureLock = Any()
    @Volatile var scheduledRemoval = false

    var future: Future<*>? = null
        get() {
            synchronized(this.futureLock) {
                while (field == null) {
                    try {
                        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
                        (this.futureLock as Object).wait()
                    } catch (ignored: InterruptedException) {
                    }
                }
            }
            return field
        }
        set(future) {
            synchronized(this.futureLock) {
                field = future
                @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
                (this.futureLock as Object).notifyAll()
            }
        }

    override fun getUniqueId(): UUID = this.uniqueId
    override fun getName(): String = this.name
    override fun getTask(): LanternTask = this.task

    override fun cancel(): Boolean = cancel(false)
    override fun isCancelled(): Boolean = this.future!!.isCancelled

    fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        val future = this.future!!
        if (future.isDone)
            return false
        // If the task is successfully cancelled, just
        // remove it from the scheduler
        if (future.cancel(mayInterruptIfRunning)) {
            this.scheduler.remove(this)
            return true
        } else {
            this.scheduledRemoval = true
        }
        return false
    }

    override fun toString(): String = ToStringHelper("ScheduledTask")
            .add("uniqueId", this.uniqueId)
            .add("name", this.name)
            .add("task", this.task)
            .toString()
}
