package org.lanternpowered.server.world;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.RejectedExecutionException;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.storage.WorldProperties;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;

public class LanternWorldManager {

    private final List<WorldEntry> worlds = new CopyOnWriteArrayList<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final Phaser tickBegin = new Phaser(1);
    private final Phaser tickEnd = new Phaser(1);

    private volatile int currentTick = -1;

    private class WorldEntry {

        private final LanternWorld world;
        private WorldThread thread;

        private WorldEntry(LanternWorld world) {
            this.world = world;
        }
    }

    private class WorldThread extends Thread {

        private final LanternWorld world;

        public WorldThread(LanternWorld world) {
            super("Lantern-World-" + world.getName());
            this.world = world;
        }

        @Override
        public void run() {
            try {
                while (!this.isInterrupted() && !tickEnd.isTerminated()) {
                    tickBegin.arriveAndAwaitAdvance();
                    try {
                        this.world.pulse();
                    } catch (Exception e) {
                        LanternGame.log().error("Error occurred while pulsing world " + this.world.getName(), e);
                    } finally {
                        tickEnd.arriveAndAwaitAdvance();
                    }
                }
            } finally {
                tickBegin.arriveAndDeregister();
                tickEnd.arriveAndDeregister();
            }
        }
    }

    public void interrupt() {
        this.tickBegin.forceTermination();
        this.tickEnd.forceTermination();
        for (WorldEntry ent : this.worlds) {
            if (ent.thread != null) {
                ent.thread.interrupt();
            }
        }
    }

    private Runnable tickEndTask = new Runnable() {

        @Override
        public void run() {
            // Mark ourselves as arrived so world threads automatically trigger advance once done
            int endPhase = tickEnd.arriveAndAwaitAdvance();
            if (endPhase != currentTick + 1) {
                LanternGame.log().warn("Tick end barrier " + endPhase + " has advanced differently from tick begin barrier: "
                        + currentTick + 1);
            }
        }
    };

    public void pulse() {
        try {
            this.tickEnd.awaitAdvanceInterruptibly(this.currentTick);
            this.currentTick = this.tickBegin.arrive();

            try {
                this.executor.submit(this.tickEndTask);
            } catch (RejectedExecutionException ex) {
                this.interrupt();
                return;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Collection<World> getWorlds() {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<WorldProperties> getUnloadedWorlds() {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<WorldProperties> getAllWorldProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    public Optional<World> getWorld(UUID uniqueId) {
        // TODO Auto-generated method stub
        return null;
    }

    public Optional<World> getWorld(String worldName) {
        // TODO Auto-generated method stub
        return null;
    }

    public Optional<WorldProperties> getDefaultWorld() {
        // TODO Auto-generated method stub
        return null;
    }

    public Optional<World> loadWorld(String worldName) {
        // TODO Auto-generated method stub
        return null;
    }

    public Optional<World> loadWorld(UUID uniqueId) {
        // TODO Auto-generated method stub
        return null;
    }

    public Optional<World> loadWorld(WorldProperties properties) {
        // TODO Auto-generated method stub
        return null;
    }

    public Optional<WorldProperties> getWorldProperties(String worldName) {
        // TODO Auto-generated method stub
        return null;
    }

    public Optional<WorldProperties> getWorldProperties(UUID uniqueId) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean unloadWorld(World world) {
        // TODO Auto-generated method stub
        return false;
    }

    public Optional<WorldProperties> createWorld(WorldCreationSettings settings) {
        // TODO Auto-generated method stub
        return null;
    }

    public ListenableFuture<Optional<WorldProperties>> copyWorld(WorldProperties worldProperties, String copyName) {
        // TODO Auto-generated method stub
        return null;
    }

    public Optional<WorldProperties> renameWorld(WorldProperties worldProperties, String newName) {
        // TODO Auto-generated method stub
        return null;
    }

    public ListenableFuture<Boolean> deleteWorld(WorldProperties worldProperties) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean saveWorldProperties(WorldProperties properties) {
        // TODO Auto-generated method stub
        return false;
    }
}
