/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.world;

import com.flowpowered.math.vector.Vector3i;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.lanternpowered.server.data.persistence.nbt.NbtStreamUtils;
import org.lanternpowered.server.data.translator.JsonTranslator;
import org.lanternpowered.server.data.util.DataQueries;
import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.world.LanternWorldProperties.OverriddenWorldProperties;
import org.lanternpowered.server.world.difficulty.LanternDifficulty;
import org.lanternpowered.server.world.dimension.LanternDimensionType;
import org.lanternpowered.server.world.gen.LanternGeneratorType;
import org.lanternpowered.server.world.gen.flat.FlatGeneratorType;
import org.lanternpowered.server.world.rules.RuleDataTypes;
import org.lanternpowered.server.world.rules.RuleType;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.difficulty.Difficulty;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public final class LanternWorldPropertiesIO {

    private final static Gson GSON = new Gson();

    private final static String LEVEL_DATA = "level.dat";
    private final static String LEVEL_DATA_OLD = "level.dat_old";
    private final static String LEVEL_DATA_NEW = "level.dat_new";
    private final static String SPONGE_LEVEL_DATA = "level_sponge.dat";
    private final static String SPONGE_LEVEL_DATA_OLD = "level_sponge.dat_old";
    private final static String SPONGE_LEVEL_DATA_NEW = "level_sponge.dat_new";
    // private final static String BUKKIT_UUID_DATA = "uid.dat";

    // The current version of the worlds
    private final static int CURRENT_VERSION = 19133;

    // Vanilla properties
    private final static DataQuery DATA = DataQuery.of("Data");
    private final static DataQuery SEED = DataQuery.of("RandomSeed");
    private final static DataQuery INITIALIZED = DataQuery.of("initialized");
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
    private final static DataQuery DIFFICULTY_LOCKED = DataQuery.of("DifficultyLocked");
    private final static DataQuery SIZE_ON_DISK = DataQuery.of("SizeOnDisk");
    private final static DataQuery MAP_FEATURES = DataQuery.of("MapFeatures");
    private final static DataQuery GAME_MODE = DataQuery.of("GameType");

    private final static DataQuery BORDER_CENTER_X = DataQuery.of("BorderCenterX");
    private final static DataQuery BORDER_CENTER_Z = DataQuery.of("BorderCenterZ");
    private final static DataQuery BORDER_SIZE_START = DataQuery.of("BorderSize");
    private final static DataQuery BORDER_SIZE_END = DataQuery.of("BorderSizeLerpTarget");
    private final static DataQuery BORDER_SIZE_LERP_TIME = DataQuery.of("BorderSizeLerpTime");
    private final static DataQuery BORDER_DAMAGE = DataQuery.of("BorderDamagePerBlock");
    private final static DataQuery BORDER_DAMAGE_THRESHOLD = DataQuery.of("BorderSafeZone");
    private final static DataQuery BORDER_WARNING_BLOCKS = DataQuery.of("BorderWarningBlocks");
    private final static DataQuery BORDER_WARNING_TIME = DataQuery.of("BorderWarningBlocks");

    private final static DataQuery GENERATOR_NAME = DataQuery.of("generatorName");
    private final static DataQuery GENERATOR_VERSION = DataQuery.of("generatorVersion");
    private final static DataQuery GENERATOR_OPTIONS = DataQuery.of("generatorOptions");

    // Forge properties
    private final static DataQuery FORGE = DataQuery.of("Forge");
    private final static DataQuery DIMENSION_DATA = DataQuery.of("DimensionData");
    private final static DataQuery DIMENSION_ARRAY = DataQuery.of("DimensionArray");

    // Sponge properties
    private final static DataQuery UUID_MOST = DataQuery.of("uuid_most");
    private final static DataQuery UUID_LEAST = DataQuery.of("uuid_least");
    private final static DataQuery DIMENSION_TYPE = DataQuery.of("dimensionType");
    private final static DataQuery DIMENSION_INDEX = DataQuery.of("dimensionId");
    private final static DataQuery GENERATOR_MODIFIERS = DataQuery.of("generatorModifiers");
    private final static DataQuery PLAYER_UUID_TABLE = DataQuery.of("PlayerIdTable");
    private final static DataQuery ENABLED = DataQuery.of("enabled");
    private final static DataQuery KEEP_SPAWN_LOADED = DataQuery.of("keepSpawnLoaded");
    private final static DataQuery LOAD_ON_STARTUP = DataQuery.of("loadOnStartup");

    // Lantern properties
    private final static DataQuery BONUS_CHEST_ENABLED = DataQuery.of("bonusChestEnabled");
    // Extra generator options for the flat world generator type
    private final static DataQuery GENERATOR_OPTIONS_EXTRA = DataQuery.of("generatorOptionsExtra");

    // Provider class names in vanilla
    private final static String OVERWORLD = "net.minecraft.world.WorldProviderSurface";
    private final static String NETHER = "net.minecraft.world.WorldProviderHell";
    private final static String END = "net.minecraft.world.WorldProviderEnd";

    private LanternWorldPropertiesIO() {
    }

    static LevelData read(Path folder, @Nullable String worldName) throws IOException {
        Path levelFile = folder.resolve(LEVEL_DATA);
        if (!Files.exists(levelFile)) {
            levelFile = folder.resolve(LEVEL_DATA_OLD);
        }
        if (!Files.exists(levelFile)) {
            throw new FileNotFoundException("Unable to find " + LEVEL_DATA + " or " + LEVEL_DATA_OLD + "!");
        }
        DataView rootDataView;
        try {
            rootDataView = NbtStreamUtils.read(Files.newInputStream(levelFile), true);
        } catch (IOException e) {
            throw new IOException("Unable to access " + LEVEL_DATA + "!", e);
        }
        DataView dataView = rootDataView.getView(DATA).get();

        DataContainer spongeRootContainer = null;
        DataView spongeContainer = null;

        Path spongeLevelFile = folder.resolve(SPONGE_LEVEL_DATA);
        if (!Files.exists(spongeLevelFile)) {
            spongeLevelFile = folder.resolve(SPONGE_LEVEL_DATA_OLD);
        }
        if (Files.exists(spongeLevelFile)) {
            try {
                spongeRootContainer = NbtStreamUtils.read(Files.newInputStream(spongeLevelFile), true);
            } catch (IOException e) {
                Lantern.getLogger().error("Unable to access {}, ignoring...", SPONGE_LEVEL_DATA, e);
            }
            spongeContainer = spongeRootContainer.getView(DataQueries.SPONGE_DATA).orElse(null);
            if (spongeContainer != null) {
                spongeRootContainer.remove(DataQueries.SPONGE_DATA);
            }
        }

        UUID uuid = null;
        /*
         * Ignore Bukkit for now, there are more different world
         * file formats/managing
         */
        /*
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
        */

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

        final LanternWorldProperties properties = new LanternWorldProperties(uuid, worldName);
        long seed = dataView.getLong(SEED).get();
        properties.age = dataView.getLong(AGE).get();
        properties.time = dataView.getLong(TIME).orElse(properties.age % 24000);
        properties.setLastPlayedTime(dataView.getLong(LAST_PLAYED).get());
        properties.raining = dataView.getInt(RAINING).get() > 0;
        properties.rainTime = dataView.getInt(RAIN_TIME).get();
        properties.thundering = dataView.getInt(THUNDERING).get() > 0;
        properties.thunderTime = dataView.getInt(THUNDER_TIME).get();
        properties.clearWeatherTime = dataView.getInt(CLEAR_WEATHER_TIME).get();
        boolean hardcore = dataView.getInt(HARDCORE).get() > 0;
        properties.mapFeatures = dataView.getInt(MAP_FEATURES).get() > 0;
        properties.initialized = dataView.getInt(INITIALIZED).get() > 0;
        byte gameModeId = dataView.getInt(GAME_MODE).get().byteValue();
        GameMode gameMode = GameModes.SURVIVAL;
        for (GameMode gameMode0 : Sponge.getRegistry().getAllOf(GameMode.class)) {
            if (((LanternGameMode) gameMode0).getInternalId() == gameModeId) {
                gameMode = gameMode0;
                break;
            }
        }
        byte difficultyId = dataView.getInt(DIFFICULTY).get().byteValue();
        Difficulty difficulty = Difficulties.NORMAL;
        for (Difficulty difficulty0 : Sponge.getRegistry().getAllOf(Difficulty.class)) {
            if (((LanternDifficulty) difficulty0).getInternalId() == difficultyId) {
                difficulty = difficulty0;
                break;
            }
        }
        if (dataView.contains(DIFFICULTY_LOCKED)) {
            properties.difficultyLocked = dataView.getInt(DIFFICULTY_LOCKED).get() > 0;
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

        if (dataView.contains(GENERATOR_NAME)) {
            final String genName0 = dataView.getString(GENERATOR_NAME).get();
            String genName = genName0;
            if (genName.indexOf(':') == -1) {
                genName = "minecraft:" + genName;
            }
            properties.generatorType = (LanternGeneratorType) Sponge.getRegistry()
                    .getType(GeneratorType.class, genName).orElse(null);
            if (properties.generatorType != null && dataView.contains(GENERATOR_OPTIONS)) {
                String options = dataView.getString(GENERATOR_OPTIONS).get();
                String customSettings = null;
                if (genName0.equalsIgnoreCase("flat")) {
                    customSettings = options;
                    // Added in the lantern-server to allow to attach
                    // custom generator options to the flat generator
                    if (dataView.contains(GENERATOR_OPTIONS_EXTRA)) {
                        options = dataView.getString(GENERATOR_OPTIONS_EXTRA).get();
                    } else {
                        options = "";
                    }
                }
                if (!options.isEmpty()) {
                    try {
                        JsonObject json = GSON.fromJson(options, JsonObject.class);
                        properties.generatorSettings = JsonTranslator.instance().translateFrom(json).copy();
                    } catch (Exception e) {
                        Lantern.getLogger().warn("Unknown generator settings format \"{}\" for type {}, using defaults...",
                                options, genName);
                        e.printStackTrace();
                    }
                } else {
                    properties.generatorSettings = new MemoryDataContainer();
                }
                if (customSettings != null && properties.generatorSettings != null) {
                    properties.generatorSettings.set(FlatGeneratorType.SETTINGS, customSettings);
                }
            }
        }

        // Get the spawn position
        final Optional<Integer> spawnX = dataView.getInt(SPAWN_X);
        final Optional<Integer> spawnY = dataView.getInt(SPAWN_Y);
        final Optional<Integer> spawnZ = dataView.getInt(SPAWN_Z);
        if (spawnX.isPresent() && spawnY.isPresent() && spawnZ.isPresent()) {
            properties.spawnPosition = new Vector3i(spawnX.get(), spawnY.get(), spawnZ.get());
        }

        // Get the game rules
        final DataView rulesView = dataView.getView(GAME_RULES).orElse(null);
        if (rulesView != null) {
            for (Entry<DataQuery, Object> en : rulesView.getValues(false).entrySet()) {
                try {
                    properties.getRules()
                            .getOrCreateRule(RuleType.getOrCreate(en.getKey().toString(), RuleDataTypes.STRING, ""))
                            .setRawValue((String) en.getValue());
                } catch (IllegalArgumentException e) {
                    Lantern.getLogger().warn("An error occurred while loading a game rule (" + en.getKey().toString() +
                            ") this one will be skipped", e);
                }
            }
        }

        Boolean enabled = null;
        Boolean keepSpawnLoaded = null;
        Boolean loadOnStartup = null;
        List<String> generatorModifiers = null;

        Integer dimensionId = null;

        // Get the sponge properties
        if (spongeContainer != null) {
            properties.properties = spongeRootContainer.copy().remove(DataQueries.SPONGE_DATA);
            if (spongeContainer.contains(ENABLED)) {
                enabled = spongeContainer.getInt(ENABLED).get() > 0;
            }
            if (spongeContainer.contains(KEEP_SPAWN_LOADED)) {
                keepSpawnLoaded = spongeContainer.getInt(KEEP_SPAWN_LOADED).get() > 0;
            }
            if (spongeContainer.contains(KEEP_SPAWN_LOADED)) {
                loadOnStartup = spongeContainer.getInt(LOAD_ON_STARTUP).get() > 0;
            }
            if (spongeContainer.contains(GENERATOR_MODIFIERS)) {
                generatorModifiers = spongeContainer.getStringList(GENERATOR_MODIFIERS).get();
            }
            dimensionId = spongeContainer.getInt(DIMENSION_INDEX).orElse(null);
            // This can be null, this is provided in the lantern-server
            String dimensionType = spongeContainer.getString(DIMENSION_TYPE).get();
            if (dimensionType.equalsIgnoreCase(OVERWORLD)) {
                properties.dimensionType = (LanternDimensionType<?>) DimensionTypes.OVERWORLD;
            } else if (dimensionType.equalsIgnoreCase(NETHER)) {
                properties.dimensionType = (LanternDimensionType<?>) DimensionTypes.NETHER;
            } else if (dimensionType.equalsIgnoreCase(END)) {
                properties.dimensionType = (LanternDimensionType<?>) DimensionTypes.THE_END;
            } else {
                properties.dimensionType = (LanternDimensionType<?>) Sponge.getRegistry()
                        .getType(DimensionType.class, dimensionType).orElse(null);
            }

            if (spongeContainer.contains(PLAYER_UUID_TABLE)) {
                List<DataView> views = spongeContainer.getViewList(PLAYER_UUID_TABLE).get();
                for (DataView view : views) {
                    long most = view.getLong(UUID_MOST).get();
                    long least = view.getLong(UUID_LEAST).get();
                    properties.pendingUniqueIds.add(new UUID(most, least));
                }
            }

            if (spongeContainer.contains(BONUS_CHEST_ENABLED)) {
                properties.bonusChestEnabled = spongeContainer.getInt(BONUS_CHEST_ENABLED).get() > 0;
            }
        } else {
            properties.properties = new MemoryDataContainer();
        }

        // There is no dimension type found, falling back to overworld
        if (properties.dimensionType == null) {
            properties.dimensionType = (LanternDimensionType<?>) DimensionTypes.OVERWORLD;
        }

        // Fall back to the default generator type
        if (properties.generatorType == null) {
            properties.generatorType = (LanternGeneratorType) properties.dimensionType.getDefaultGeneratorType();
        }
        // Fall back to the default generator settings
        if (properties.generatorSettings == null) {
            properties.generatorSettings = properties.generatorType.getGeneratorSettings();
        }
        // Create extra properties container if not present
        if (properties.properties == null) {
            properties.properties = new MemoryDataContainer();
        }

        BitSet dimensionMap = null;
        if (rootDataView.contains(FORGE)) {
            final DataView forgeView = rootDataView.getView(FORGE).get();
            if (forgeView.contains(DIMENSION_DATA)) {
                dimensionMap = new BitSet(LanternWorldManager.DIMENSION_MAP_SIZE);
                final int[] intArray = (int[]) forgeView.getView(DIMENSION_DATA).get().get(DIMENSION_ARRAY).get();
                for (int i = 0; i < intArray.length; i++) {
                    for (int j = 0; j < Integer.SIZE; j++) {
                        dimensionMap.set(i * Integer.SIZE + j, (intArray[i] & (1 << j)) != 0);
                    }
                }
            }
        }

        return new LevelData(properties, dimensionId, dimensionMap, new OverriddenWorldProperties(
                difficulty, gameMode, hardcore, seed, enabled, keepSpawnLoaded, loadOnStartup, generatorModifiers));
    }

    static void write(Path folder, LevelData levelData) throws IOException {
        final LanternWorldProperties properties = levelData.properties;

        final DataContainer container = new MemoryDataContainer();
        final DataView dataView = container.createView(DATA);

        dataView.set(SEED, properties.getSeed());
        dataView.set(VERSION, CURRENT_VERSION);
        dataView.set(NAME, properties.getWorldName());
        final DataView rulesView = dataView.createView(GAME_RULES);
        for (Entry<String, String> en : properties.getGameRules().entrySet()) {
            rulesView.set(DataQuery.of(en.getKey()), en.getValue());
        }
        dataView.set(AGE, properties.age);
        dataView.set(TIME, properties.time);
        dataView.set(RAINING, (byte) (properties.raining ? 1 : 0));
        dataView.set(RAIN_TIME, properties.rainTime);
        dataView.set(THUNDERING, (byte) (properties.thundering ? 1 : 0));
        dataView.set(THUNDER_TIME, properties.thunderTime);
        dataView.set(HARDCORE, (byte) (properties.isHardcore() ? 1 : 0));
        dataView.set(CLEAR_WEATHER_TIME, properties.clearWeatherTime);
        dataView.set(LAST_PLAYED, properties.getLastPlayedTime());
        dataView.set(SIZE_ON_DISK, 0L);
        dataView.set(INITIALIZED, (byte) (properties.initialized ? 1 : 0));
        String genId = properties.generatorType.getId();
        if (genId.startsWith("minecraft:")) {
            genId = genId.replaceFirst("minecraft:", "");
        }
        dataView.set(GENERATOR_NAME, genId);
        // The default world generator has a version of one
        dataView.set(GENERATOR_VERSION, genId.equalsIgnoreCase("default") ? 1 : 0);
        // The flat world generator has a different settings format
        if (genId.equalsIgnoreCase("flat")) {
            dataView.set(GENERATOR_OPTIONS, properties.generatorSettings.getString(FlatGeneratorType.SETTINGS).get());
            dataView.set(GENERATOR_OPTIONS_EXTRA, GSON.toJson(JsonTranslator.instance().translateData(
                    properties.generatorSettings.copy().remove(FlatGeneratorType.SETTINGS))));
        } else {
            dataView.set(GENERATOR_OPTIONS, properties.generatorSettings);
        }
        dataView.set(DIFFICULTY, ((LanternDifficulty) properties.getDifficulty()).getInternalId());
        dataView.set(DIFFICULTY_LOCKED, (byte) (properties.difficultyLocked ? 1 : 0));
        dataView.set(GAME_MODE, ((LanternGameMode) properties.getGameMode()).getInternalId());
        dataView.set(MAP_FEATURES, (byte) (properties.mapFeatures ? 1 : 0));
        dataView.set(BORDER_CENTER_X, properties.borderCenterX);
        dataView.set(BORDER_CENTER_Z, properties.borderCenterZ);
        dataView.set(BORDER_DAMAGE, properties.borderDamage);
        dataView.set(BORDER_DAMAGE_THRESHOLD, properties.borderDamageThreshold);
        dataView.set(BORDER_SIZE_END, properties.borderDiameterEnd);
        dataView.set(BORDER_SIZE_START, properties.getWorldBorderDiameter());
        dataView.set(BORDER_SIZE_LERP_TIME, properties.getWorldBorderTimeRemaining());
        dataView.set(BORDER_WARNING_BLOCKS, properties.borderWarningDistance);
        dataView.set(BORDER_WARNING_TIME, properties.borderWarningTime);
        final Vector3i spawn = properties.spawnPosition;
        dataView.set(SPAWN_X, spawn.getX());
        dataView.set(SPAWN_Y, spawn.getY());
        dataView.set(SPAWN_Z, spawn.getZ());
        if (levelData.dimensionMap != null) {
            final BitSet dimensionMap = levelData.dimensionMap;
            final DataView dimensionData = container.createView(FORGE).createView(DIMENSION_DATA);
            int[] data = new int[(dimensionMap.length() + Integer.SIZE - 1) / Integer.SIZE];
            for (int i = 0; i < data.length; i++) {
                int val = 0;
                for (int j = 0; j < Integer.SIZE; j++) {
                    val |= dimensionMap.get(i * Integer.SIZE + j) ? (1 << j) : 0;
                }
                data[i] = val;
            }
            dimensionData.set(DIMENSION_ARRAY, data);
        }
        Path levelFileNew = folder.resolve(LEVEL_DATA_NEW);
        Path levelFileOld = folder.resolve(LEVEL_DATA_OLD);
        Path levelFile = folder.resolve(LEVEL_DATA);
        NbtStreamUtils.write(container, Files.newOutputStream(levelFileNew), true);
        if (Files.exists(levelFileOld)) {
            Files.delete(levelFileOld);
        }
        if (Files.exists(levelFile)) {
            Files.move(levelFile, levelFileOld);
        }
        Files.move(levelFileNew, levelFile);
        if (Files.exists(levelFileNew)) {
            Files.delete(levelFileNew);
        }
        final DataContainer spongeRootContainer = properties.properties.copy();
        final DataView spongeContainer = spongeRootContainer.createView(DataQueries.SPONGE_DATA);
        spongeContainer.set(NAME, properties.getWorldName());
        spongeContainer.set(UUID_MOST, properties.uniqueId.getMostSignificantBits());
        spongeContainer.set(UUID_LEAST, properties.uniqueId.getLeastSignificantBits());
        spongeContainer.set(BONUS_CHEST_ENABLED, (byte) (properties.bonusChestEnabled ? 1 : 0));
        if (levelData.dimensionId != null) {
            spongeContainer.set(DIMENSION_INDEX, levelData.dimensionId);
        }
        spongeContainer.set(DIMENSION_TYPE, properties.dimensionType.getId());
        spongeContainer.set(GENERATOR_MODIFIERS, properties.generatorModifiers.stream().map(
                CatalogType::getId).collect(Collectors.toList()));
        spongeContainer.set(PLAYER_UUID_TABLE, properties.pendingUniqueIds.stream().map(
                uuid -> new MemoryDataContainer()
                            .set(UUID_MOST, uuid.getMostSignificantBits())
                            .set(UUID_LEAST, uuid.getLeastSignificantBits()))
                .collect(Collectors.toList()));
        levelFileNew = folder.resolve(SPONGE_LEVEL_DATA_NEW);
        levelFileOld = folder.resolve(SPONGE_LEVEL_DATA_OLD);
        levelFile = folder.resolve(SPONGE_LEVEL_DATA);
        NbtStreamUtils.write(spongeRootContainer, Files.newOutputStream(levelFileNew), true);
        if (Files.exists(levelFileOld)) {
            Files.delete(levelFileOld);
        }
        if (Files.exists(levelFile)) {
            Files.move(levelFile, levelFileOld);
        }
        Files.move(levelFileNew, levelFile);
        if (Files.exists(levelFileNew)) {
            Files.delete(levelFileNew);
        }
    }

    public static class LevelData {

        public final LanternWorldProperties properties;
        // The id of the dimension, if already set before
        @Nullable public final Integer dimensionId;
        // The map with all the dimension ids, this is only present on the root world (normally)
        @Nullable public final BitSet dimensionMap;
        // The level data that should be applied to the world config
        @Nullable public final OverriddenWorldProperties configLevelData;

        public LevelData(LanternWorldProperties properties, @Nullable Integer dimensionId,
                @Nullable BitSet dimensionMap, @Nullable OverriddenWorldProperties configLevelData) {
            this.configLevelData = configLevelData;
            this.dimensionMap = dimensionMap;
            this.dimensionId = dimensionId;
            this.properties = properties;
        }
    }

}
