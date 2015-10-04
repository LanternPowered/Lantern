package org.lanternpowered.server.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import org.lanternpowered.server.data.io.nbt.NbtDataContainerInputStream;
import org.lanternpowered.server.data.io.nbt.NbtDataContainerOutputStream;
import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.world.DimensionType;

import com.flowpowered.math.vector.Vector3i;

public class WorldPropertiesSerializer {

    private final static String LEVEL_DATA = "level.dat";
    private final static String SPONGE_LEVEL_DATA = "level_sponge.dat";
    private final static String BUKKIT_UUID_DATA = "uid.dat";

    // Vanilla properties
    private final static DataQuery SEED = DataQuery.of("RandomSeed");
    private final static DataQuery THUNDERING = DataQuery.of("thundering");
    private final static DataQuery THUNDER_TIME = DataQuery.of("thunderTime");
    private final static DataQuery RAINING = DataQuery.of("raining");
    private final static DataQuery RAIN_TIME = DataQuery.of("rainTime");
    private final static DataQuery AGE = DataQuery.of("Time");
    private final static DataQuery TIME = DataQuery.of("DayTime");
    private final static DataQuery SPAWN_X = DataQuery.of("SpawnX");
    private final static DataQuery SPAWN_Y = DataQuery.of("SpawnY");
    private final static DataQuery SPAWN_Z = DataQuery.of("SpawnZ");
    private final static DataQuery GAME_RULES = DataQuery.of("GameRules");

    // Sponge properties
    private final static DataQuery ECOSYSTEM = DataQuery.of("Sponge");
    private final static DataQuery UUID_MOST = DataQuery.of("uuid_most");
    private final static DataQuery UUID_LEAST = DataQuery.of("uuid_least");
    private final static DataQuery ENABLED = DataQuery.of("enabled");
    private final static DataQuery KEEP_SPAWN_LOADED = DataQuery.of("keepSpawnLoaded");
    private final static DataQuery LOAD_ON_STARTUP = DataQuery.of("loadOnStartup");
    private final static DataQuery DIMENSION_TYPE = DataQuery.of("dimensionType");
    private final static DataQuery GENERATOR_MODIFIERS = DataQuery.of("generatorModifiers");

    private WorldPropertiesSerializer() {
    }

    public static boolean exists(File folder) {
        return new File(folder, LEVEL_DATA).exists();
    }

    public static LanternWorldProperties read(File folder, @Nullable String worldName) throws IOException {
        File levelFile = new File(folder, LEVEL_DATA);
        if (!levelFile.exists()) {
            throw new FileNotFoundException("Unable to find " + LEVEL_DATA + "!");
        }

        DataContainer container;
        try (NbtDataContainerInputStream is = new NbtDataContainerInputStream(
                new DataInputStream(new FileInputStream(levelFile)))) {
            container = is.read();
        } catch (IOException e) {
            throw new IOException("Unable to access " + LEVEL_DATA + "!", e);
        }

        DataContainer spongeRootContainer = null;
        DataView spongeContainer = null;

        File spongeLevelFile = new File(folder, SPONGE_LEVEL_DATA);
        if (spongeLevelFile.exists()) {
            try (NbtDataContainerInputStream is = new NbtDataContainerInputStream(
                    new DataInputStream(new FileInputStream(spongeLevelFile)))) {
                spongeRootContainer = is.read();
            } catch (IOException e) {
                LanternGame.log().error("Unable to access {}, ignoring...", SPONGE_LEVEL_DATA, e);
            }
            spongeContainer = spongeRootContainer.getView(ECOSYSTEM).orElse(null);
            if (spongeContainer != null) {
                spongeRootContainer.remove(ECOSYSTEM);
            }
        }

        UUID uuid = null;
        // The uuid storage bukkit used, try this one first
        File uuidFile = new File(folder, BUKKIT_UUID_DATA);
        if (uuidFile.exists()) {
            try (DataInputStream in = new DataInputStream(new FileInputStream(uuidFile))) {
                uuid = new UUID(in.readLong(), in.readLong());
            } catch (IOException e) {
                LanternGame.log().error("Unable to access {}, ignoring...", BUKKIT_UUID_DATA, e);
            }
            uuidFile.delete();
        }

        // Try for the sponge (lantern) storage format
        if (uuid == null && spongeContainer != null) {
            Long most = spongeContainer.getLong(UUID_MOST).orElse(null);
            Long least = spongeContainer.getLong(UUID_LEAST).orElse(null);
            if (most != null && least != null) {
                uuid = new UUID(most, least);
            }
        }

        // Generate a new one if needed
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }

        LanternWorldProperties properties = new LanternWorldProperties();
        // Get the world name
        properties.name = worldName == null ? folder.getName() : worldName;
        // Set the uuid
        properties.uniqueId = uuid;
        // Get the world seed
        properties.seed = container.getLong(SEED).orElse(new Random().nextLong());
        // Get the world age
        properties.age = container.getLong(AGE).orElse(0L);
        // Get the world time
        properties.time = container.getLong(TIME).orElse(0L);

        // Get the spawn position
        Optional<Integer> spawnX = container.getInt(SPAWN_X);
        Optional<Integer> spawnY = container.getInt(SPAWN_Y);
        Optional<Integer> spawnZ = container.getInt(SPAWN_Z);
        if (spawnX.isPresent() && spawnY.isPresent() && spawnZ.isPresent()) {
            properties.spawnPosition = new Vector3i(spawnX.get(), spawnY.get(), spawnZ.get());
        }

        // Get the game rules
        DataView rulesView = container.getView(GAME_RULES).orElse(null);
        if (rulesView != null) {
            for (Entry<DataQuery, Object> en : rulesView.getValues(false).entrySet()) {
                properties.rules.newRule(en.getKey().toString()).set(en.getValue());
            }
        }

        // Get the sponge properties
        if (spongeContainer != null) {
            properties.properties = spongeRootContainer;
            properties.enabled = spongeContainer.getBoolean(ENABLED).get();
            properties.keepSpawnLoaded = spongeContainer.getBoolean(KEEP_SPAWN_LOADED).get();
            properties.loadOnStartup = spongeContainer.getBoolean(LOAD_ON_STARTUP).get();

            String dimensionType = spongeContainer.getString(DIMENSION_TYPE).get();
            for (DimensionType type : LanternGame.get().getRegistry().getAllOf(DimensionType.class)) {
                // Why not just use the id?
                if (type.getDimensionClass().getCanonicalName().equalsIgnoreCase(dimensionType)) {
                    properties.dimensionType = type;
                    break;
                }
            }
        } else {
            properties.properties = new MemoryDataContainer();
        }

        return properties;
    }

    public static void write(File folder, LanternWorldProperties properties) throws IOException {
        DataContainer container = new MemoryDataContainer();
        container.set(SEED, properties.seed);
        DataView rules = container.createView(GAME_RULES);
        for (Entry<String, String> en : properties.rules.getValues().entrySet()) {
            rules.set(DataQuery.of(en.getKey()), en.getValue());
        }
        Vector3i spawn = properties.spawnPosition;
        container.set(SPAWN_X, spawn.getX());
        container.set(SPAWN_Y, spawn.getY());
        container.set(SPAWN_Z, spawn.getZ());
        File levelFile = new File(folder, LEVEL_DATA);
        if (levelFile.exists()) {
            levelFile.delete();
        }
        try (NbtDataContainerOutputStream os = new NbtDataContainerOutputStream(
                new DataOutputStream(new FileOutputStream(levelFile)))) {
            os.write(container);
            os.flush();
        }
        DataContainer spongeRootContainer = properties.properties.copy();
        DataView spongeContainer = spongeRootContainer.createView(ECOSYSTEM);
        spongeContainer.set(UUID_MOST, properties.uniqueId.getMostSignificantBits());
        spongeContainer.set(UUID_LEAST, properties.uniqueId.getLeastSignificantBits());
        spongeContainer.set(ENABLED, properties.enabled);
        spongeContainer.set(KEEP_SPAWN_LOADED, properties.keepSpawnLoaded);
        spongeContainer.set(LOAD_ON_STARTUP, properties.loadOnStartup);
        // Why not just use the id?
        spongeContainer.set(DIMENSION_TYPE, properties.dimensionType.getDimensionClass().getCanonicalName());
        levelFile = new File(folder, SPONGE_LEVEL_DATA);
        if (levelFile.exists()) {
            levelFile.delete();
        }
        try (NbtDataContainerOutputStream os = new NbtDataContainerOutputStream(
                new DataOutputStream(new FileOutputStream(levelFile)))) {
            os.write(spongeRootContainer);
            os.flush();
        }
    }
}
