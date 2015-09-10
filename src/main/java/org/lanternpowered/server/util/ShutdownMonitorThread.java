package org.lanternpowered.server.util;

import java.util.Map;

import org.lanternpowered.server.game.LanternGame;

/**
 * Thread started on shutdown that monitors for and kills rogue non-daemon threads.
 */
public class ShutdownMonitorThread extends Thread {

    /**
     * The delay in milliseconds until leftover threads are killed.
     */
    private static final int DELAY = 8000;

    public ShutdownMonitorThread() {
        this.setName("ShutdownMonitorThread");
        this.setDaemon(true);
    }

    @Override
    public void run() {
        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            LanternGame.log().error("Shutdown monitor interrupted", e);
            System.exit(0);
            return;
        }

        LanternGame.log().warn("Still running after shutdown, finding rogue threads...");

        final Map<Thread, StackTraceElement[]> traces = Thread.getAllStackTraces();
        for (Map.Entry<Thread, StackTraceElement[]> entry : traces.entrySet()) {
            final Thread thread = entry.getKey();
            final StackTraceElement[] stack = entry.getValue();

            if (thread.isDaemon() || !thread.isAlive() || stack.length == 0) {
                // won't keep JVM from exiting
                continue;
            }

            LanternGame.log().warn("Rogue thread: " + thread);
            for (StackTraceElement trace : stack) {
                LanternGame.log().warn("    at " + trace);
            }

            // ask nicely to kill them
            thread.interrupt();
            // wait for them to die on their own
            if (thread.isAlive()) {
                try {
                    thread.join(1000);
                } catch (InterruptedException ex) {
                    LanternGame.log().error("Shutdown monitor interrupted", ex);
                    System.exit(0);
                    return;
                }
            }
        }
        // kill them forcefully
        System.exit(0);
    }

}