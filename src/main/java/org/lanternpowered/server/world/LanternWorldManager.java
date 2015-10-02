package org.lanternpowered.server.world;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.RejectedExecutionException;

import javax.annotation.Nullable;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.storage.WorldProperties;

import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class LanternWorldManager {

    private final List<WorldEntry> worlds = new CopyOnWriteArrayList<>();
    private final ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    private final ConcurrentMap<String, WorldProperties> propertiesByName = Maps.newConcurrentMap();
    private final ConcurrentMap<UUID, WorldProperties> propertiesByUUID = Maps.newConcurrentMap();

    private final Phaser tickBegin = new Phaser(1);
    private final Phaser tickEnd = new Phaser(1);
    @Nullable private final String defaultWorld;
    private final File folder;

    private volatile int currentTick = -1;

    public LanternWorldManager(File folder, @Nullable String defaultWorld) {
        this.defaultWorld = defaultWorld;
        this.folder = folder;
    }

    private void addProperties(WorldProperties properties) {
        this.propertiesByName.put(properties.getWorldName(), properties);
        this.propertiesByUUID.put(properties.getUniqueId(), properties);
    }

    private void removeProperties(String name) {
        WorldProperties properties = this.propertiesByName.remove(name);
        if (properties != null) {
            this.propertiesByUUID.remove(properties.getUniqueId());
        }
    }

    private void removeProperties(UUID uniqueId) {
        WorldProperties properties = this.propertiesByUUID.remove(uniqueId);
        if (properties != null) {
            this.propertiesByName.remove(properties.getWorldName());
        }
    }

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

    public void shutdown() {
        this.tickBegin.forceTermination();
        this.tickEnd.forceTermination();
        for (WorldEntry ent : this.worlds) {
            if (ent.thread != null) {
                ent.thread.interrupt();
            }
        }
        this.executor.shutdown();
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
                this.shutdown();
                return;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public LanternWorld addWorld(LanternWorld world) {
        final WorldEntry entry = new WorldEntry(world);
        this.worlds.add(entry);
        try {
            entry.thread = new WorldThread(world);
            this.tickBegin.register();
            this.tickEnd.register();
            entry.thread.start();
            return world;
        } catch (Throwable t) {
            this.tickBegin.arriveAndDeregister();
            this.tickEnd.arriveAndDeregister();
            this.worlds.remove(entry);
            return null;
        }
    }

    public Collection<World> getWorlds() {
        return ImmutableList.copyOf(Collections2.transform(this.worlds, entry -> entry.world));
    }

    @Nullable
    private WorldProperties propertiesFromFolder(File folder, boolean silent) {
        String name = folder.getName();
        try {
            if (this.propertiesByName.containsKey(folder)) {
                return this.propertiesByName.get(name);
            }
            WorldProperties properties = WorldPropertiesSerializer.read(folder, name);
            this.addProperties(properties);
            return properties;
        } catch (IOException e) {
            if (!silent) {
                LanternGame.log().error("Unable to load world properties for: " + folder.getName());
            }
        }
        return null;
    }

    public Collection<WorldProperties> getUnloadedWorlds() {
        List<WorldProperties> properties = Lists.newArrayList(this.propertiesByName.values());
        for (WorldEntry entry : this.worlds) {
            properties.remove(entry.world.getProperties());
        }
        return ImmutableList.copyOf(properties);
    }

    public Collection<WorldProperties> getAllWorldProperties() {
        for (File file : folder.listFiles(file -> file.isDirectory())) {
            this.propertiesFromFolder(file, true);
        }
        return ImmutableList.copyOf(this.propertiesByName.values());
    }

    public Optional<World> getWorld(UUID uniqueId) {
        return Optional.fromNullable(this.getWorlds().stream().filter(
                world -> world.getUniqueId().equals(uniqueId)).findFirst().orElse(null));
    }

    public Optional<World> getWorld(String worldName) {
        return Optional.fromNullable(this.getWorlds().stream().filter(
                world -> world.getName().equals(worldName)).findFirst().orElse(null));
    }

    public Optional<WorldProperties> getDefaultWorld() {
        if (this.defaultWorld == null) {
            return Optional.absent();
        }
        return Optional.fromNullable(this.propertiesFromFolder(new File(this.folder, this.defaultWorld), true));
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
        return Optional.fromNullable(this.propertiesFromFolder(new File(folder, worldName), true));
    }

    public Optional<WorldProperties> getWorldProperties(UUID uniqueId) {
        this.getAllWorldProperties(); // Load the properties of all the worlds
        return Optional.fromNullable(this.propertiesByUUID.get(uniqueId));
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
        return this.executor.submit(new Callable<Optional<WorldProperties>>() {

            @Override
            public Optional<WorldProperties> call() throws Exception {
                File worldFolder = new File(folder, copyName);
                if (worldFolder.exists() && worldFolder.list().length != 0) {
                    return Optional.absent();
                }
                String fromWorldName = worldProperties.getWorldName();
                File fromWorldFolder = new File(folder, fromWorldName);
                if (!fromWorldFolder.exists() || worldFolder.list().length == 0) {
                    return Optional.absent();
                }
                World world = getWorld(fromWorldName).orNull();
                if (world != null) {
                    // TODO: Save
                    // TODO: Lock saving
                }
                copyFolder(fromWorldFolder, worldFolder);
                if (world != null) {
                    // TODO: Unlock saving
                }
                LanternWorldProperties properties = WorldPropertiesSerializer.read(worldFolder, copyName);
                addProperties(properties);
                return Optional.of(properties);
            }
        });
    }

    public Optional<WorldProperties> renameWorld(WorldProperties worldProperties, String newName) {
        // TODO Auto-generated method stub
        return null;
    }

    public ListenableFuture<Boolean> deleteWorld(WorldProperties worldProperties) {
        return this.executor.submit(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                String name = worldProperties.getWorldName();
                World world = getWorld(name).orNull();
                if (world != null) {
                    return false;
                }
                File worldFolder = new File(folder, name);
                if (!worldFolder.exists()) {
                    return true;
                }
                boolean flag = deleteFolder(worldFolder);
                removeProperties(name);
                return flag;
            }
        });
    }

    private static void copyFolder(File from, File to) throws IOException {
        for (File file : from.listFiles()) {
            File newFile = new File(to, file.getName());
            if (file.isDirectory()) {
                file.mkdirs();
                copyFolder(file, newFile);
            } else {
                Files.copy(file, newFile);
            }
        }
    }

    private static boolean deleteFolder(File folder) {
        boolean success = true;
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                success &= deleteFolder(file);
            } else {
                success &= file.delete();
            }
        }
        success &= folder.delete();
        return success;
    }

    public boolean saveWorldProperties(WorldProperties properties) {
        try {
            WorldPropertiesSerializer.write(new File(folder, properties.getWorldName()),
                    (LanternWorldProperties) properties);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
