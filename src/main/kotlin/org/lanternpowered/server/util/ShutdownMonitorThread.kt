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
package org.lanternpowered.server.util

import org.lanternpowered.server.game.Lantern
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

/**
 * Thread started on shutdown that monitors for and kills rogue non-daemon threads.
 */
class ShutdownMonitorThread(timeout: Long, unit: TimeUnit) : Thread("shutdown-monitor") {

    private val timeoutMillis: Long = unit.toMillis(timeout)

    init {
        this.isDaemon = true
    }

    override fun run() {
        try {
            sleep(this.timeoutMillis)
        } catch (e: InterruptedException) {
            Lantern.getLogger().error("Shutdown monitor interrupted", e)
            exitProcess(0)
        }
        Lantern.getLogger().warn("Still running after shutdown, finding rogue threads...")
        val traces = getAllStackTraces()
        for ((thread, stack) in traces) {
            if (thread.isDaemon || !thread.isAlive || stack.isEmpty()) {
                // won't keep JVM from exiting
                continue
            }
            if (thread is LanternThread) {
                Lantern.getLogger().warn("Rogue thread (lantern): $thread")
                Lantern.getLogger().warn("    construction location:")
                val constructionSource = thread.constructionSite
                for (trace in constructionSource.stackTrace) {
                    Lantern.getLogger().warn("        at $trace")
                }
            } else {
                Lantern.getLogger().warn("Rogue thread: $thread")
            }
            Lantern.getLogger().warn("    interrupt location:")
            for (trace in stack) {
                Lantern.getLogger().warn("        at $trace")
            }

            // ask nicely to kill them
            thread.interrupt()
            // wait for them to die on their own
            if (thread.isAlive) {
                try {
                    thread.join(1000)
                } catch (ex: InterruptedException) {
                    Lantern.getLogger().error("Shutdown monitor interrupted", ex)
                    exitProcess(0)
                }
            }
        }
        // kill them forcefully
        exitProcess(0)
    }
}
