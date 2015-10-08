package org.lanternpowered.server.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.annotation.Nullable;

import org.lanternpowered.server.data.io.nbt.NbtDataContainerInputStream;
import org.lanternpowered.server.data.io.nbt.NbtDataContainerOutputStream;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.game.LanternGameRegistry;
import org.lanternpowered.server.world.difficulty.LanternDifficulty;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;

public class LanternWorldPropertiesIO {

    private final static String LEVEL_DATA = "level.dat";
    private final static String SPONGE_LEVEL_DATA = "level_sponge.dat";
    private final static String BUKKIT_UUID_DATA = "uid.dat";

    // The current version of the worlds
    private final static int CURRENT_VERSION = 19133;

    // Vanilla properties
    private final static DataQuery DATA = DataQuery.of("Data");
    private final static DataQuery SEED = DataQuery.of("RandomSeed");
    private final static DataQuery CLEAR_WEATHER_TIME = DataQuery.of("clearWeatherTime");
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
    private final static DataQuery HARDCORE = DataQuery.of("hardcore");
    private final static DataQuery VERSION = DataQuery.of("version");
    private final static DataQuery NAME = DataQuery.of("LevelName");
    private final static DataQuery LAST_PLAYED = DataQuery.of("LastPlayed");
    private final static DataQuery DIFFICULTY = DataQuery.of("Difficulty");
    private final static DataQuery SIZE_ON_DISK = DataQuery.of("SizeOnDisk");

    private final static DataQuery BORDER_CENTER_X = DataQuery.of("BorderCenterX");
    private final static DataQuery BORDER_CENTER_Z = DataQuery.of("BorderCenterZ");
    private final static DataQuery BORDER_SIZE_START = DataQuery.of("BorderSize");
    private final static DataQuery BORDER_SIZE_END = DataQuery.of("BorderSizeLerpTarget");
    private final static DataQuery BORDER_SIZE_LERP_TIME = DataQuery.of("BorderSizeLerpTime");
    private final static DataQuery BORDER_DAMAGE = DataQuery.of("BorderDamagePerBlock");
    private final static DataQuery BORDER_DAMAGE_THRESHOLD = DataQuery.of("BorderSafeZone");
    private final static DataQuery BORDER_WARNING_BLOCKS = DataQuery.of("BorderWarningBlocks");
    private final static DataQuery BORDER_WARNING_TIME = DataQuery.of("BorderWarningBlocks");

    // Sponge properties
    private final static DataQuery ECOSYSTEM = DataQuery.of("Sponge");
    private final static DataQuery UUID_MOST = DataQuery.of("uuid_most");
    private final static DataQuery UUID_LEAST = DataQuery.of("uuid_least");
    private final static DataQuery ENABLED = DataQuery.of("enabled");
    private final static DataQuery KEEP_SPAWN_LOADED = DataQuery.of("keepSpawnLoaded");
    private final static DataQuery LOAD_ON_STARTUP = DataQuery.of("loadOnStartup");
    private final static DataQuery DIMENSION_TYPE = DataQuery.of("dimensionType");
    private final static DataQuery GENERATOR_MODIFIERS = DataQuery.of("generatorModifiers");

    private LanternWorldPropertiesIO() {
    }

    public static boolean exists(File folder) {
        return new File(folder, LEVEL_DATA).exists();
    }

    public static LanternWorldProperties read(File folder, @Nullable String worldName) throws IOException {
        File levelFile = new File(folder, LEVEL_DATA);
        if (!levelFile.exists()) {
            throw new FileNotFoundException("Unable to find " + LEVEL_DATA + "!");
        }

        DataView dataView;
        try (NbtDataContainerInputStream is = new NbtDataContainerInputStream(
                new DataInputStream(new GZIPInputStream(new FileInputStream(levelFile))))) {
            dataView = is.read().getView(DATA).get();
        } catch (IOException e) {
            throw new IOException("Unable to access " + LEVEL_DATA + "!", e);
        }

        DataContainer spongeRootContainer = null;
        DataView spongeContainer = null;

        File spongeLevelFile = new File(folder, SPONGE_LEVEL_DATA);
        if (spongeLevelFile.exists()) {
            try (NbtDataContainerInputStream is = new NbtDataContainerInputStream(
                    new DataInputStream(new GZIPInputStream(new FileInputStream(spongeLevelFile))))) {
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

        // Get the world name if not overridden
        if (worldName == null) {
            worldName = dataView.getString(NAME).get();
        }

        LanternWorldProperties properties = new LanternWorldProperties();
        properties.uniqueId = uuid;
        properties.name = worldName;
        properties.seed = dataView.getLong(SEED).get();
        properties.age = dataView.getLong(AGE).get();
        properties.time = dataView.getLong(TIME).orElse(properties.age % 24000);
        properties.raining = dataView.getInt(RAINING).get() > 0;
        properties.rainTime = dataView.getInt(RAIN_TIME).get();
        properties.thundering = dataView.getInt(THUNDERING).get() > 0;
        properties.thunderTime = dataView.getInt(THUNDER_TIME).get();
        properties.clearWeatherTime = dataView.getInt(CLEAR_WEATHER_TIME).get();
        properties.hardcore = dataView.getInt(HARDCORE).get() > 0;
        byte difficulty = dataView.getInt(DIFFICULTY).get().byteValue();
        for (Difficulty difficulty0 : LanternGame.get().getRegistry().getAllOf(Difficulty.class)) {
            if (((LanternDifficulty) difficulty0).getInternalId() == difficulty) {
                properties.difficulty = difficulty0;
                break;
            }
        }
        if (dataView.contains(BORDER_CENTER_X)) {
            properties.borderCenterX = dataView.getDouble(BORDER_CENTER_X).get();
        }
        if (dataView.contains(BORDER_CENTER_Z)) {
            properties.borderCenterZ = dataView.getDouble(BORDER_CENTER_Z).get();
        }
        if (dataView.contains(BORDER_SIZE_START)) {
            properties.borderDiameterStart = dataView.getDouble(BORDER_SIZE_START).get();
        }
        if (dataView.contains(BORDER_SIZE_END)) {
            properties.borderDiameterEnd = dataView.getDouble(BORDER_SIZE_END).get();
        }
        if (dataView.contains(BORDER_SIZE_LERP_TIME)) {
            properties.borderLerpTime = dataView.getLong(BORDER_SIZE_LERP_TIME).get();
        }
        if (dataView.contains(BORDER_DAMAGE)) {
            properties.borderDamage = dataView.getDouble(BORDER_DAMAGE).get();
        }
        if (dataView.contains(BORDER_DAMAGE_THRESHOLD)) {
            properties.borderDamage = dataView.getDouble(BORDER_DAMAGE_THRESHOLD).get();
        }
        if (dataView.contains(BORDER_WARNING_BLOCKS)) {
            properties.borderWarningDistance = dataView.getInt(BORDER_WARNING_BLOCKS).get();
        }
        if (dataView.contains(BORDER_WARNING_TIME)) {
            properties.borderWarningTime = dataView.getInt(BORDER_WARNING_TIME).get();
        }

        // Get the spawn position
        Optional<Integer> spawnX = dataView.getInt(SPAWN_X);
        Optional<Integer> spawnY = dataView.getInt(SPAWN_Y);
        Optional<Integer> spawnZ = dataView.getInt(SPAWN_Z);
        if (spawnX.isPresent() && spawnY.isPresent() && spawnZ.isPresent()) {
            properties.spawnPosition = new Vector3i(spawnX.get(), spawnY.get(), spawnZ.get());
        }

        // Get the game rules
        DataView rulesView = dataView.getView(GAME_RULES).orElse(null);
        if (rulesView != null) {
            for (Entry<DataQuery, Object> en : rulesView.getValues(false).entrySet()) {
                properties.rules.newRule(en.getKey().toString()).set(en.getValue());
            }
        }

        // Get the sponge properties
        if (spongeContainer != null) {
            properties.properties = spongeRootContainer;
            properties.enabled = spongeContainer.getInt(ENABLED).get() > 0;
            properties.keepSpawnLoaded = spongeContainer.getInt(KEEP_SPAWN_LOADED).get() > 0;
            properties.loadOnStartup = spongeContainer.getInt(LOAD_ON_STARTUP).get() > 0;

            String dimensionType = spongeContainer.getString(DIMENSION_TYPE).get();
            for (DimensionType type : LanternGame.get().getRegistry().getAllOf(DimensionType.class)) {
                // Why not just use the id?
                if (type.getDimensionClass().getCanonicalName().equalsIgnoreCase(dimensionType)) {
                    properties.dimensionType = type;
                    break;
                }
            }

            List<String> modifiers = spongeContainer.getStringList(GENERATOR_MODIFIERS).get();
            ImmutableList.Builder<WorldGeneratorModifier> genModifiers = ImmutableList.builder();
            LanternGameRegistry registry = LanternGame.get().getRegistry();
            for (String modifier : modifiers) {
                Optional<WorldGeneratorModifier> genModifier = registry.getType(WorldGeneratorModifier.class, modifier);
                if (genModifier.isPresent()) {
                    genModifiers.add(genModifier.get());
                } else {
                    LanternGame.log().error("World generator modifier with id " + modifier +
                            " not found. Missing plugin?");
                }
            }
        } else {
            properties.properties = new MemoryDataContainer();
        }

        return properties;
    }

    public static void write(File folder, LanternWorldProperties properties) throws IOException {
        DataContainer container = new MemoryDataContainer();
        DataView dataView = container.createView(DATA);

        dataView.set(SEED, properties.seed);
        dataView.set(VERSION, CURRENT_VERSION);
        DataView rulesView = container.createView(GAME_RULES);
        for (Entry<String, String> en : properties.rules.getValues().entrySet()) {
            rulesView.set(DataQuery.of(en.getKey()), en.getValue());
        }
        dataView.set(AGE, properties.age);
        dataView.set(TIME, properties.time);
        dataView.set(RAINING, properties.raining);
        dataView.set(RAIN_TIME, properties.rainTime);
        dataView.set(THUNDERING, properties.thundering);
        dataView.set(THUNDER_TIME, properties.thunderTime);
        dataView.set(HARDCORE, properties.hardcore);
        dataView.set(CLEAR_WEATHER_TIME, properties.clearWeatherTime);
        dataView.set(LAST_PLAYED, System.currentTimeMillis());
        dataView.set(SIZE_ON_DISK, 0L);
        dataView.set(DIFFICULTY, ((LanternDifficulty) properties.difficulty).getInternalId());
        dataView.set(BORDER_CENTER_X, properties.borderCenterX);
        dataView.set(BORDER_CENTER_Z, properties.borderCenterZ);
        dataView.set(BORDER_DAMAGE, properties.borderDamage);
        dataView.set(BORDER_DAMAGE_THRESHOLD, properties.borderDamageThreshold);
        dataView.set(BORDER_SIZE_END, properties.borderDiameterEnd);
        dataView.set(BORDER_SIZE_START, properties.getWorldBorderDiameter());
        dataView.set(BORDER_SIZE_LERP_TIME, properties.getWorldBorderTimeRemaining());
        dataView.set(BORDER_WARNING_BLOCKS, properties.borderWarningDistance);
        container.set(BORDER_WARNING_TIME, properties.borderWarningTime);
        Vector3i spawn = properties.spawnPosition;
        dataView.set(SPAWN_X, spawn.getX());
        dataView.set(SPAWN_Y, spawn.getY());
        dataView.set(SPAWN_Z, spawn.getZ());
        File levelFile = new File(folder, LEVEL_DATA);
        if (levelFile.exists()) {
            levelFile.delete();
        }
        try (NbtDataContainerOutputStream os = new NbtDataContainerOutputStream(
                new DataOutputStream(new GZIPOutputStream(new FileOutputStream(levelFile))))) {
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
                new DataOutputStream(new GZIPOutputStream(new FileOutputStream(levelFile))))) {
            os.write(spongeRootContainer);
            os.flush();
        }
    }
}
