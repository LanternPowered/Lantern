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
package org.lanternpowered.server.world;

import com.flowpowered.math.vector.Vector3i;
import com.google.gson.Gson;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.lanternpowered.server.config.world.WorldConfig;
import org.lanternpowered.server.data.DataQueries;
import org.lanternpowered.server.data.io.IOHelper;
import org.lanternpowered.server.data.persistence.json.JsonDataFormat;
import org.lanternpowered.server.data.persistence.nbt.NbtStreamUtils;
import org.lanternpowered.server.data.world.MoonPhase;
import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.type.entity.player.GameModeRegistryModule;
import org.lanternpowered.server.game.registry.type.world.DifficultyRegistryModule;
import org.lanternpowered.server.util.UncheckedThrowables;
import org.lanternpowered.server.world.difficulty.LanternDifficulty;
import org.lanternpowered.server.world.dimension.LanternDimensionType;
import org.lanternpowered.server.world.gen.flat.AbstractFlatGeneratorType;
import org.lanternpowered.server.world.weather.LanternWeather;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.SerializationBehaviors;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.gamerule.GameRule;
import org.spongepowered.api.world.gen.GeneratorType;
import org.spongepowered.api.world.gen.GeneratorTypes;
import org.spongepowered.api.world.teleport.PortalAgentType;
import org.spongepowered.api.world.teleport.PortalAgentTypes;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.Weathers;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
final class LanternWorldPropertiesIO {

    private final static String LEVEL_DATA = "level.dat";
    private final static String SPONGE_LEVEL_DATA = "level_sponge.dat";
    private final static String BUKKIT_UUID_DATA = "uid.dat";

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
    private final static DataQuery UUID_MOST = DataQuery.of("UUIDMost");
    private final static DataQuery UUID_LEAST = DataQuery.of("UUIDLeast");
    private final static DataQuery OLD_UUID_MOST = DataQuery.of("uuid_most");
    private final static DataQuery OLD_UUID_LEAST = DataQuery.of("uuid_least");
    private final static DataQuery PORTAL_AGENT_TYPE = DataQuery.of("portalAgentType");
    private final static DataQuery DIMENSION_TYPE = DataQuery.of("dimensionType");
    private final static DataQuery DIMENSION_INDEX = DataQuery.of("dimensionId");
    private final static DataQuery GENERATOR_MODIFIERS = DataQuery.of("generatorModifiers");
    private final static DataQuery TRACKER_UUID_TABLE = DataQuery.of("PlayerIdTable");
    private final static DataQuery ENABLED = DataQuery.of("enabled");
    private final static DataQuery KEEP_SPAWN_LOADED = DataQuery.of("keepSpawnLoaded");
    private final static DataQuery LOAD_ON_STARTUP = DataQuery.of("loadOnStartup");
    private final static DataQuery GENERATE_BONUS_CHEST = DataQuery.of("GenerateBonusChest");
    private final static DataQuery SERIALIZATION_BEHAVIOR = DataQuery.of("serializationBehavior");

    // Extra generator options for the flat world generator type
    private final static DataQuery GENERATOR_OPTIONS_EXTRA = DataQuery.of("generatorOptionsExtra");

    private final static DataQuery LANTERN = DataQuery.of("LanternData");
    private final static DataQuery WEATHER = DataQuery.of("weather");
    private final static DataQuery WEATHER_TYPE = DataQuery.of("type");
    private final static DataQuery WEATHER_RUNNING_DURATION = DataQuery.of("running");
    private final static DataQuery WEATHER_REMAINING_DURATION = DataQuery.of("remaining");
    private final static DataQuery MOON_PHASE = DataQuery.of("moonPhase");

    // Provider class names in vanilla
    private final static String OVERWORLD = "net.minecraft.world.WorldProviderSurface";
    private final static String NETHER = "net.minecraft.world.WorldProviderHell";
    private final static String END = "net.minecraft.world.WorldProviderEnd";

    private LanternWorldPropertiesIO() {
    }

    static LevelData read(Path directory, @Nullable String worldName, @Nullable UUID uniqueId) throws IOException {
        final DataView rootDataView = IOHelper.read(directory.resolve(LEVEL_DATA), file -> NbtStreamUtils.read(Files.newInputStream(file), true))
                .orElseThrow(() -> new FileNotFoundException("Unable to find " + LEVEL_DATA + "!"));
        final DataView dataView = rootDataView.getView(DATA).get();

        if (worldName == null) {
            worldName = dataView.getString(NAME).get();
        }
        final DataView spongeRootDataView = IOHelper.read(directory.resolve(SPONGE_LEVEL_DATA),
                file -> NbtStreamUtils.read(Files.newInputStream(file), true)).orElse(null);
        final DataView spongeContainer = spongeRootDataView != null ? spongeRootDataView.getView(DataQueries.SPONGE_DATA).orElse(null) : null;

        if (uniqueId == null) {
            // Try for the sponge (lantern) storage format
            if (spongeContainer != null) {
                final Long most = spongeContainer.getLong(UUID_MOST).orElseGet(() -> spongeContainer.getLong(OLD_UUID_MOST).orElse(null));
                final Long least = spongeContainer.getLong(UUID_LEAST).orElseGet(() -> spongeContainer.getLong(OLD_UUID_LEAST).orElse(null));
                if (most != null && least != null) {
                    uniqueId = new UUID(most, least);
                }
            }
            // The uuid storage bukkit used, try this one first
            final Path uuidFile;
            if (uniqueId == null && Files.exists((uuidFile = directory.resolve(BUKKIT_UUID_DATA)))) {
                try (DataInputStream in = new DataInputStream(Files.newInputStream(uuidFile))) {
                    uniqueId = new UUID(in.readLong(), in.readLong());
                } catch (IOException e) {
                    Lantern.getLogger().error("Unable to access {}, ignoring...", BUKKIT_UUID_DATA, e);
                }
                Files.delete(uuidFile);
            }
            if (uniqueId == null) {
                uniqueId = UUID.randomUUID();
            }
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

        final Integer dimensionId = spongeContainer != null ? spongeContainer.getInt(DIMENSION_INDEX).orElse(null) : null;
        return new LevelData(worldName, uniqueId, rootDataView, spongeRootDataView, dimensionId, dimensionMap);
    }

    static LanternWorldProperties convert(LevelData levelData, WorldConfig worldConfig, boolean copyLevelSettingsToConfig) {
        final LanternWorldProperties properties = new LanternWorldProperties(levelData.uniqueId, levelData.worldName, worldConfig);

        final DataView dataView = levelData.worldData.getView(DATA).get();
        final DataView spongeRootDataView = levelData.spongeWorldData;
        final DataView spongeDataView;
        if (spongeRootDataView != null) {
            spongeDataView = spongeRootDataView.getView(DataQueries.SPONGE_DATA).orElse(null);
            if (spongeDataView != null) {
                spongeDataView.remove(DataQueries.SPONGE_DATA);
            }
        } else {
            spongeDataView = null;
        }
        final DataView lanternDataView = spongeDataView == null ? null : spongeDataView.getView(LANTERN).orElse(null);

        properties.setLastPlayedTime(dataView.getLong(LAST_PLAYED).get());
        properties.mapFeatures = dataView.getInt(MAP_FEATURES).get() > 0;
        properties.setInitialized(dataView.getInt(INITIALIZED).get() > 0);
        dataView.getInt(DIFFICULTY_LOCKED).ifPresent(v -> properties.setDifficultyLocked(v > 0));

        final LanternWorldBorder border = properties.getWorldBorder();
        dataView.getDouble(BORDER_CENTER_X).ifPresent(v -> border.centerX = v);
        dataView.getDouble(BORDER_CENTER_Z).ifPresent(v -> border.centerZ = v);
        dataView.getDouble(BORDER_SIZE_START).ifPresent(v -> border.diameterStart = v);
        dataView.getDouble(BORDER_SIZE_END).ifPresent(v -> border.diameterEnd = v);
        dataView.getLong(BORDER_SIZE_LERP_TIME).ifPresent(v -> border.lerpTime = v);
        dataView.getDouble(BORDER_DAMAGE).ifPresent(v -> border.damage = v);
        dataView.getDouble(BORDER_DAMAGE_THRESHOLD).ifPresent(v -> border.damageThreshold = v);
        dataView.getInt(BORDER_WARNING_BLOCKS).ifPresent(v -> border.warningDistance = v);
        dataView.getInt(BORDER_WARNING_TIME).ifPresent(v -> border.warningTime = Duration.ofSeconds(v));

        if (spongeRootDataView != null) {
            properties.setAdditionalProperties(spongeRootDataView.copy().remove(DataQueries.SPONGE_DATA));
        }

        // Get the sponge properties
        if (spongeDataView != null) {
            // This can be null, this is provided in the lantern-server
            final String dimensionTypeId = spongeDataView.getString(DIMENSION_TYPE).get();
            if (dimensionTypeId.equalsIgnoreCase(OVERWORLD)) {
                properties.setDimensionType(DimensionTypes.OVERWORLD);
            } else if (dimensionTypeId.equalsIgnoreCase(NETHER)) {
                properties.setDimensionType(DimensionTypes.NETHER);
            } else if (dimensionTypeId.equalsIgnoreCase(END)) {
                properties.setDimensionType(DimensionTypes.THE_END);
            } else {
                final DimensionType dimensionType = Sponge.getRegistry().getType(
                        DimensionType.class, CatalogKey.resolve(dimensionTypeId)).orElse(null);
                if (dimensionType == null) {
                    Lantern.getLogger().warn("Could not find a dimension type with id {} for the world {}, falling back to overworld...",
                            dimensionTypeId, levelData.worldName);
                }
                properties.setDimensionType(dimensionType == null ? DimensionTypes.OVERWORLD : dimensionType);
            }

            PortalAgentType portalAgentType = null;
            if (spongeDataView.contains(PORTAL_AGENT_TYPE)) {
                final String portalAgentTypeId = spongeDataView.getString(PORTAL_AGENT_TYPE).get();
                portalAgentType = Sponge.getRegistry().getType(
                        PortalAgentType.class, CatalogKey.resolve(portalAgentTypeId)).orElse(null);
                if (portalAgentType == null) {
                    Lantern.getLogger().warn("Could not find a portal agent type with id {} for the world {}, falling back to default...",
                            portalAgentTypeId, levelData.worldName);
                }
            }
            properties.setPortalAgentType(portalAgentType == null ? PortalAgentTypes.DEFAULT : portalAgentType);

            spongeDataView.getInt(GENERATE_BONUS_CHEST).ifPresent(v -> properties.setGenerateBonusChest(v > 0));
            spongeDataView.getInt(SERIALIZATION_BEHAVIOR).ifPresent(v -> properties.setSerializationBehavior(
                    v == 0 ? SerializationBehaviors.MANUAL : v == 1 ? SerializationBehaviors.AUTOMATIC : SerializationBehaviors.NONE));

            // Tracker
            final Optional<List<DataView>> optTrackerUniqueIdViews = spongeDataView.getViewList(TRACKER_UUID_TABLE);
            if (optTrackerUniqueIdViews.isPresent()) {
                final List<DataView> trackerUniqueIdViews = optTrackerUniqueIdViews.get();
                final Object2IntMap<UUID> trackerUniqueIds = properties.getTrackerIdAllocator().getUniqueIds();
                final List<UUID> uniqueIdsByIndex = properties.getTrackerIdAllocator().getUniqueIdsByIndex();

                for (DataView view : trackerUniqueIdViews) {
                    UUID uniqueId = null;
                    if (!view.isEmpty()) {
                        final long most = view.getLong(UUID_MOST).get();
                        final long least = view.getLong(UUID_LEAST).get();
                        uniqueId = new UUID(most, least);
                        trackerUniqueIds.put(uniqueId, uniqueIdsByIndex.size());
                    }
                    uniqueIdsByIndex.add(uniqueId);
                }
            }
        }

        // Weather
        final WeatherData weatherData = properties.getWeatherData();
        if (lanternDataView != null) {
            final DataView weatherView = lanternDataView.getView(WEATHER).get();

            final String weatherTypeId = weatherView.getString(WEATHER_TYPE).get();
            final Optional<Weather> weatherType = Sponge.getRegistry().getType(
                    Weather.class, CatalogKey.resolve(weatherTypeId));
            if (weatherType.isPresent()) {
                weatherData.setWeather((LanternWeather) weatherType.get());
            } else {
                Lantern.getLogger().info("Unknown weather type: {}, the server will default to {}",
                        weatherTypeId, weatherData.getWeather().getKey());
            }
            weatherData.setRunningDuration(weatherView.getLong(WEATHER_RUNNING_DURATION).get());
            weatherData.setRemainingDuration(weatherView.getLong(WEATHER_REMAINING_DURATION).get());
        } else {
            final boolean raining = dataView.getInt(RAINING).get() > 0;
            final long rainTime = dataView.getLong(RAIN_TIME).get();
            final boolean thundering = dataView.getInt(THUNDERING).get() > 0;
            final long thunderTime = dataView.getLong(THUNDER_TIME).get();
            final long clearWeatherTime = dataView.getLong(CLEAR_WEATHER_TIME).get();
            if (thundering) {
                weatherData.setWeather((LanternWeather) Weathers.THUNDER_STORM);
                weatherData.setRemainingDuration(thunderTime);
            } else if (raining) {
                weatherData.setWeather((LanternWeather) Weathers.RAIN);
                weatherData.setRemainingDuration(rainTime);
            } else {
                weatherData.setRemainingDuration(clearWeatherTime);
            }
        }

        // Time
        final TimeData timeData = properties.getTimeData();
        final long age = dataView.getLong(AGE).get();
        timeData.setAge(age);
        final long time = dataView.getLong(TIME).orElse(age);
        timeData.setDayTime(time);
        if (lanternDataView != null && lanternDataView.contains(MOON_PHASE)) {
            timeData.setMoonPhase(MoonPhase.valueOf(lanternDataView.getString(MOON_PHASE).get().toUpperCase()));
        } else {
            timeData.setMoonPhase(MoonPhase.values()[(int) (time / TimeUniverse.TICKS_IN_A_DAY) % 8]);
        }

        // Get the spawn position
        final Optional<Integer> spawnX = dataView.getInt(SPAWN_X);
        final Optional<Integer> spawnY = dataView.getInt(SPAWN_Y);
        final Optional<Integer> spawnZ = dataView.getInt(SPAWN_Z);
        if (spawnX.isPresent() && spawnY.isPresent() && spawnZ.isPresent()) {
            properties.setSpawnPosition(new Vector3i(spawnX.get(), spawnY.get(), spawnZ.get()));
        }

        // Get the game rules
        final DataView rulesView = dataView.getView(GAME_RULES).orElse(null);
        if (rulesView != null) {
            for (GameRule gameRule : Sponge.getRegistry().getAllOf(GameRule.class)) {
                try {
                    final Object value = rulesView.getObject(DataQuery.of(gameRule.getName()),
                            gameRule.getValueType().getRawType()).orElse(null);
                    properties.setGameRule(gameRule, value);
                } catch (Exception e) {
                    Lantern.getLogger().warn("An error occurred while loading a game rule ({}) this one will be skipped",
                            gameRule.getName(), e);
                }
            }
        }

        if (copyLevelSettingsToConfig) {
            worldConfig.getGeneration().setSeed(dataView.getLong(SEED).get());
            worldConfig.setGameMode(GameModeRegistryModule.get().getByInternalId(dataView.getInt(GAME_MODE).get())
                    .orElse(GameModes.SURVIVAL));
            worldConfig.setHardcore(dataView.getInt(HARDCORE).get() > 0);
            worldConfig.setDifficulty(DifficultyRegistryModule.get().getByInternalId(dataView.getInt(DIFFICULTY).get())
                    .orElse(Difficulties.NORMAL));

            if (dataView.contains(GENERATOR_NAME)) {
                final String genName0 = dataView.getString(GENERATOR_NAME).get();
                final String genName = genName0.indexOf(':') == -1 ? "minecraft:" + genName0 : genName0;
                final GeneratorType generatorType = Sponge.getRegistry().getType(GeneratorType.class, CatalogKey.resolve(genName))
                        .orElse(properties.getDimensionType().getDefaultGeneratorType());
                DataContainer generatorSettings = null;
                if (dataView.contains(GENERATOR_OPTIONS)) {
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
                            generatorSettings = JsonDataFormat.readContainer(options, false);
                        } catch (Exception e) {
                            Lantern.getLogger().warn("Unknown generator settings format \"{}\" for type {}, using defaults...",
                                    options, genName, e);
                        }
                    }
                    if (generatorSettings == null) {
                        generatorSettings = generatorType.getGeneratorSettings();
                    }
                    if (customSettings != null) {
                        generatorSettings.set(AbstractFlatGeneratorType.SETTINGS, customSettings);
                    }
                } else {
                    generatorSettings = generatorType.getGeneratorSettings();
                }

                worldConfig.getGeneration().setGeneratorType(generatorType);
                worldConfig.getGeneration().setGeneratorSettings(generatorSettings);
                worldConfig.setLowHorizon(generatorType == GeneratorTypes.FLAT);
            }

            if (spongeDataView != null) {
                spongeDataView.getInt(ENABLED).ifPresent(v -> worldConfig.setWorldEnabled(v > 0));
                worldConfig.setKeepSpawnLoaded(spongeDataView.getInt(KEEP_SPAWN_LOADED).map(v -> v > 0)
                        .orElse(properties.getDimensionType().getKeepSpawnLoaded()));
                spongeDataView.getInt(LOAD_ON_STARTUP).ifPresent(v -> worldConfig.setKeepSpawnLoaded(v > 0));
                spongeDataView.getStringList(GENERATOR_MODIFIERS).ifPresent(v -> {
                    final List<String> modifiers = worldConfig.getGeneration().getGenerationModifiers();
                    modifiers.clear();
                    modifiers.addAll(v);
                    properties.updateWorldGenModifiers(modifiers);
                });
            } else {
                final LanternDimensionType dimensionType = properties.getDimensionType();
                worldConfig.setKeepSpawnLoaded(dimensionType.getKeepSpawnLoaded());
                worldConfig.setDoesWaterEvaporate(dimensionType.getDoesWaterEvaporate());
            }
        }

        return properties;
    }

    static LevelData convert(LanternWorldProperties properties, @Nullable Integer dimensionId, @Nullable BitSet dimensionMap) {
        final DataContainer rootContainer = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
        final DataView dataView = rootContainer.createView(DATA);
        final DataContainer spongeRootContainer = properties.getAdditionalProperties().copy();
        final DataView spongeContainer = spongeRootContainer.createView(DataQueries.SPONGE_DATA);
        final DataView lanternDataView = spongeContainer.createView(LANTERN);

        dataView.set(SEED, properties.getSeed());
        dataView.set(VERSION, CURRENT_VERSION);
        final DataView rulesView = dataView.createView(GAME_RULES);
        for (Entry<GameRule<?>, ?> en : properties.getGameRules().entrySet()) {
            rulesView.set(DataQuery.of(en.getKey().getName()), en.getValue());
        }

        // Weather
        final WeatherData weatherData = properties.getWeatherData();
        final boolean raining = properties.isRaining();
        final boolean thunderStorm = properties.isThundering();
        dataView.set(RAINING, (byte) (properties.isRaining() ? 1 : 0));
        dataView.set(RAIN_TIME, properties.getRainTime());
        dataView.set(THUNDERING, (byte) (properties.isThundering() ? 1 : 0));
        dataView.set(THUNDER_TIME, properties.getThunderTime());
        dataView.set(HARDCORE, (byte) (properties.isHardcore() ? 1 : 0));
        dataView.set(CLEAR_WEATHER_TIME, raining || thunderStorm ? 0 : weatherData.getRemainingDuration());
        final DataView weatherDataView = lanternDataView.createView(WEATHER);
        weatherDataView.set(WEATHER_TYPE, weatherData.getWeather().getKey());
        weatherDataView.set(WEATHER_REMAINING_DURATION, weatherData.getRemainingDuration());
        weatherDataView.set(WEATHER_RUNNING_DURATION, weatherData.getRunningDuration());

        // Time
        final TimeData timeData = properties.getTimeData();
        dataView.set(TIME, timeData.getDayTime());
        dataView.set(AGE, timeData.getAge());
        lanternDataView.set(MOON_PHASE, timeData.getMoonPhase().toString().toLowerCase());

        dataView.set(LAST_PLAYED, properties.getLastPlayedTime());
        dataView.set(SIZE_ON_DISK, 0L);
        dataView.set(INITIALIZED, (byte) (properties.isInitialized() ? 1 : 0));
        String generatorId = properties.getGeneratorType().getKey().toString();
        if (generatorId.startsWith("minecraft:")) {
            generatorId = generatorId.replaceFirst("minecraft:", "");
        }
        dataView.set(GENERATOR_NAME, generatorId);
        // The default world generator has a version of one
        dataView.set(GENERATOR_VERSION, generatorId.equalsIgnoreCase("default") ? 1 : 0);
        // The flat world generator has a different settings format
        if (generatorId.equalsIgnoreCase("flat")) {
            dataView.set(GENERATOR_OPTIONS, properties.getGeneratorSettings().getString(AbstractFlatGeneratorType.SETTINGS).get());
            try {
                dataView.set(GENERATOR_OPTIONS_EXTRA, JsonDataFormat.writeAsString(
                        properties.getGeneratorSettings().copy().remove(AbstractFlatGeneratorType.SETTINGS)));
            } catch (IOException e) {
                throw UncheckedThrowables.throwUnchecked(e);
            }
        } else {
            dataView.set(GENERATOR_OPTIONS, properties.getGeneratorSettings());
        }
        dataView.set(DIFFICULTY, ((LanternDifficulty) properties.getDifficulty()).getInternalId());
        dataView.set(DIFFICULTY_LOCKED, (byte) (properties.isDifficultyLocked() ? 1 : 0));
        dataView.set(GAME_MODE, ((LanternGameMode) properties.getGameMode()).getInternalId());
        dataView.set(MAP_FEATURES, (byte) (properties.mapFeatures ? 1 : 0));
        final LanternWorldBorder border = properties.getWorldBorder();
        dataView.set(BORDER_CENTER_X, border.centerX);
        dataView.set(BORDER_CENTER_Z, border.centerZ);
        dataView.set(BORDER_DAMAGE, border.damage);
        dataView.set(BORDER_DAMAGE_THRESHOLD, border.damageThreshold);
        dataView.set(BORDER_SIZE_END, border.diameterEnd);
        dataView.set(BORDER_SIZE_START, border.getDiameter());
        dataView.set(BORDER_SIZE_LERP_TIME, border.getTimeRemainingMillis());
        dataView.set(BORDER_WARNING_BLOCKS, border.getRoundedWarningDistance());
        dataView.set(BORDER_WARNING_TIME, border.getWarningTimeSeconds());
        final Vector3i spawn = properties.getSpawnPosition();
        dataView.set(SPAWN_X, spawn.getX());
        dataView.set(SPAWN_Y, spawn.getY());
        dataView.set(SPAWN_Z, spawn.getZ());
        spongeContainer.set(GENERATE_BONUS_CHEST, (byte) (properties.doesGenerateBonusChest() ? 1 : 0));
        spongeContainer.set(DIMENSION_TYPE, properties.getDimensionType().getKey());
        spongeContainer.set(PORTAL_AGENT_TYPE, properties.getPortalAgentType().getKey());
        spongeContainer.set(GENERATOR_MODIFIERS, properties.generatorModifiers.stream().map(
                CatalogType::getKey).collect(Collectors.toList()));

        // Serialization behavior
        short serializationBehaviorId = 1;
        if (properties.getSerializationBehavior() == SerializationBehaviors.MANUAL) {
            serializationBehaviorId = 0;
        } else if (properties.getSerializationBehavior() == SerializationBehaviors.NONE) {
            serializationBehaviorId = -1;
        }
        spongeContainer.set(SERIALIZATION_BEHAVIOR, serializationBehaviorId);

        // Tracker
        final List<DataView> trackerUniqueIdViews = new ArrayList<>();
        final List<UUID> uniqueIdsByIndex = properties.getTrackerIdAllocator().getUniqueIdsByIndex();

        for (UUID uniqueId : uniqueIdsByIndex) {
            final DataView uniqueIdView = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
            if (uniqueId != null) {
                uniqueIdView.set(UUID_MOST, uniqueId.getMostSignificantBits());
                uniqueIdView.set(UUID_LEAST, uniqueId.getLeastSignificantBits());
            }
            trackerUniqueIdViews.add(uniqueIdView);
        }
        spongeContainer.set(TRACKER_UUID_TABLE, trackerUniqueIdViews);

        return new LevelData(properties.getWorldName(), properties.getUniqueId(), rootContainer,
                spongeRootContainer, dimensionId, dimensionMap);
    }

    static void write(Path folder, LevelData levelData) throws IOException {
        final DataView rootDataView = levelData.worldData;
        final DataView dataView = rootDataView.getView(DATA)
                .orElseGet(() -> rootDataView.createView(DATA));
        dataView.set(NAME, levelData.worldName);
        if (levelData.dimensionMap != null) {
            final BitSet dimensionMap = levelData.dimensionMap;
            final DataView dimensionData = rootDataView.createView(FORGE).createView(DIMENSION_DATA);
            final int[] data = new int[(dimensionMap.length() + Integer.SIZE - 1) / Integer.SIZE];
            for (int i = 0; i < data.length; i++) {
                int val = 0;
                for (int j = 0; j < Integer.SIZE; j++) {
                    val |= dimensionMap.get(i * Integer.SIZE + j) ? (1 << j) : 0;
                }
                data[i] = val;
            }
            dimensionData.set(DIMENSION_ARRAY, data);
        }
        IOHelper.write(folder.resolve(LEVEL_DATA), file -> {
            NbtStreamUtils.write(rootDataView, Files.newOutputStream(file), true);
            return true;
        });
        final DataView spongeRootContainer = levelData.spongeWorldData == null ?
                DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED) : levelData.spongeWorldData;
        final DataView spongeContainer = spongeRootContainer.getView(DataQueries.SPONGE_DATA)
                .orElseGet(() -> spongeRootContainer.createView(DataQueries.SPONGE_DATA));
        spongeContainer.set(UUID_MOST, levelData.uniqueId.getMostSignificantBits());
        spongeContainer.set(UUID_LEAST, levelData.uniqueId.getLeastSignificantBits());
        if (levelData.dimensionId != null) {
            spongeContainer.set(DIMENSION_INDEX, levelData.dimensionId);
        }
        IOHelper.write(folder.resolve(SPONGE_LEVEL_DATA), file -> {
            NbtStreamUtils.write(spongeRootContainer, Files.newOutputStream(file), true);
            return true;
        });
    }

    public static class LevelData {

        public final UUID uniqueId;
        public final String worldName;
        public final DataView worldData;
        @Nullable public final DataView spongeWorldData;
        // The id of the dimension, if already set before
        @Nullable public final Integer dimensionId;
        // The map with all the dimension ids, this is only present on the root world (normally)
        @Nullable public final BitSet dimensionMap;

        public LevelData(String worldName, UUID uniqueId, DataView worldData, @Nullable DataView spongeWorldData, @Nullable Integer dimensionId,
                @Nullable BitSet dimensionMap) {
            this.spongeWorldData = spongeWorldData;
            this.dimensionMap = dimensionMap;
            this.dimensionId = dimensionId;
            this.uniqueId = uniqueId;
            this.worldName = worldName;
            this.worldData = worldData;
        }
    }

}
