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
package org.lanternpowered.server.util;

import org.lanternpowered.server.game.Lantern;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Thread started on shutdown that monitors for and kills rogue non-daemon threads.
 */
public class ShutdownMonitorThread extends Thread {

    private final long timeoutMillis;

    public ShutdownMonitorThread(long timeout, TimeUnit unit) {
        this.timeoutMillis = unit.toMillis(timeout);
        setName("shutdown-monitor");
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            Thread.sleep(this.timeoutMillis);
        } catch (InterruptedException e) {
            Lantern.getLogger().error("Shutdown monitor interrupted", e);
            System.exit(0);
            return;
        }

        Lantern.getLogger().warn("Still running after shutdown, finding rogue threads...");

        final Map<Thread, StackTraceElement[]> traces = Thread.getAllStackTraces();
        for (Map.Entry<Thread, StackTraceElement[]> entry : traces.entrySet()) {
            final Thread thread = entry.getKey();
            final StackTraceElement[] stack = entry.getValue();

            if (thread.isDaemon() || !thread.isAlive() || stack.length == 0) {
                // won't keep JVM from exiting
                continue;
            }

            if (thread instanceof LanternThread) {
                final LanternThread lanternThread = (LanternThread) thread;
                Lantern.getLogger().warn("Rogue thread (lantern): " + lanternThread);
                Lantern.getLogger().warn("    construction location:");
                final Throwable constructionSource = lanternThread.getConstructionSite();
                for (StackTraceElement trace : constructionSource.getStackTrace()) {
                    Lantern.getLogger().warn("        at " + trace);
                }
            } else {
                Lantern.getLogger().warn("Rogue thread: " + thread);
            }
            Lantern.getLogger().warn("    interrupt location:");
            for (StackTraceElement trace : stack) {
                Lantern.getLogger().warn("        at " + trace);
            }

            // ask nicely to kill them
            thread.interrupt();
            // wait for them to die on their own
            if (thread.isAlive()) {
                try {
                    thread.join(1000);
                } catch (InterruptedException ex) {
                    Lantern.getLogger().error("Shutdown monitor interrupted", ex);
                    System.exit(0);
                    return;
                }
            }
        }
        // kill them forcefully
        System.exit(0);
    }

}
