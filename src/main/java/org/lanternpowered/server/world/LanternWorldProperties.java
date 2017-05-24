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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.config.world.WorldConfig;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetDifficulty;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldBorder;
import org.lanternpowered.server.world.difficulty.LanternDifficulty;
import org.lanternpowered.server.world.dimension.LanternDimensionType;
import org.lanternpowered.server.world.gen.LanternGeneratorType;
import org.lanternpowered.server.world.portal.LanternPortalAgentType;
import org.lanternpowered.server.world.rules.Rule;
import org.lanternpowered.server.world.rules.RuleDataTypes;
import org.lanternpowered.server.world.rules.RuleType;
import org.lanternpowered.server.world.rules.Rules;
import org.lanternpowered.server.world.weather.LanternWeather;
import org.lanternpowered.server.world.weather.WeatherOptions;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.GeneratorTypes;
import org.spongepowered.api.world.PortalAgentType;
import org.spongepowered.api.world.PortalAgentTypes;
import org.spongepowered.api.world.SerializationBehavior;
import org.spongepowered.api.world.SerializationBehaviors;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.Weathers;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public final class LanternWorldProperties implements WorldProperties {

    private static final int BOUNDARY = 29999984;

    // The unique id of the world
    final UUID uniqueId;

    // The world config
    WorldConfig worldConfig;

    // The rules of the world
    private final Rules rules = new Rules(this);

    private final TrackerIdAllocator trackerIdAllocator = new TrackerIdAllocator();

    // The serialization behavior
    SerializationBehavior serializationBehavior = SerializationBehaviors.AUTOMATIC;

    // The extra properties
    private DataContainer additionalProperties = DataContainer.createNew();

    // The type of the dimension
    private LanternDimensionType<?> dimensionType = (LanternDimensionType<?>) DimensionTypes.OVERWORLD;

    // The portal agent type
    private LanternPortalAgentType portalAgentType = (LanternPortalAgentType) PortalAgentTypes.DEFAULT;

    // The world generator modifiers
    ImmutableSet<WorldGeneratorModifier> generatorModifiers = ImmutableSet.of();

    // Whether the difficulty is locked
    private boolean difficultyLocked;

    // The name of the world
    private String name;

    // The spawn position
    private Vector3i spawnPosition = Vector3i.ZERO;

    // Whether the world is initialized
    private boolean initialized;
    private boolean generateBonusChest;
    private boolean commandsAllowed;
    boolean mapFeatures;

    private final TimeData timeData = new TimeData();
    private final WeatherData weatherData = new WeatherData();

    @Nullable private LanternWorld world;

    // World border properties
    double borderCenterX;
    double borderCenterZ;

    // The current radius of the border
    double borderDiameterStart = 60000000f;
    double borderDiameterEnd = this.borderDiameterStart;

    int borderWarningDistance = 5;
    int borderWarningTime = 15;

    double borderDamage = 1;
    double borderDamageThreshold = 5;

    // The remaining time will be stored in this
    // for the first world tick
    long borderLerpTime;

    // Shrink or growing times
    private long borderTimeStart = -1;
    private long borderTimeEnd;

    // The last time the world was played in
    private long lastPlayed;

    public LanternWorldProperties(String name, WorldConfig worldConfig) {
        this(UUID.randomUUID(), name, worldConfig);
    }

    public LanternWorldProperties(UUID uniqueId, String name, WorldConfig worldConfig) {
        this.worldConfig = worldConfig;
        this.uniqueId = uniqueId;
        this.name = name;
    }

    public void loadConfig() throws IOException {
        this.worldConfig.load();
        updateWorldGenModifiers(this.worldConfig.getGeneration().getGenerationModifiers());
    }

    public void update(LanternWorldArchetype worldArchetype) throws IOException {
        this.commandsAllowed = worldArchetype.areCommandsAllowed();
        this.dimensionType = worldArchetype.getDimensionType();
        this.portalAgentType = worldArchetype.getPortalAgentType();
        setGeneratorType(worldArchetype.getGeneratorType());
        this.worldConfig.getGeneration().setGeneratorSettings(worldArchetype.getGeneratorSettings());
        this.generateBonusChest = worldArchetype.doesGenerateBonusChest();
        this.mapFeatures = worldArchetype.usesMapFeatures();
        setSeed(worldArchetype.getSeed());
        this.worldConfig.setGameMode(worldArchetype.getGameMode());
        this.worldConfig.setAllowPlayerRespawns(worldArchetype.allowPlayerRespawns());
        this.worldConfig.setDifficulty(worldArchetype.getDifficulty());
        this.worldConfig.setKeepSpawnLoaded(worldArchetype.doesKeepSpawnLoaded());
        this.worldConfig.setDoesWaterEvaporate(worldArchetype.waterEvaporates());
        setGeneratorModifiers(worldArchetype.getGeneratorModifiers());
        setEnabled(worldArchetype.isEnabled());
        this.worldConfig.setPVPEnabled(worldArchetype.isPVPEnabled());
        setBuildHeight(worldArchetype.getBuildHeight());
        this.worldConfig.setHardcore(worldArchetype.isHardcore());
        this.worldConfig.setLowHorizon(worldArchetype.getGeneratorType() == GeneratorTypes.FLAT);
        this.worldConfig.save();
    }

    public void updateWorldGenModifiers(List<String> modifiers) {
        final ImmutableSet.Builder<WorldGeneratorModifier> genModifiers = ImmutableSet.builder();
        final GameRegistry registry = Sponge.getRegistry();
        for (String modifier : modifiers) {
            Optional<WorldGeneratorModifier> genModifier = registry.getType(WorldGeneratorModifier.class, modifier);
            if (genModifier.isPresent()) {
                genModifiers.add(genModifier.get());
            } else {
                Lantern.getLogger().error("World generator modifier with id " + modifier +
                        " not found. Missing plugin?");
            }
        }
        this.generatorModifiers = genModifiers.build();
    }

    /**
     * Sets the name of the world.
     *
     * @param name The name
     */
    public void setName(String name) {
        this.name = checkNotNull(name, "name");
    }

    /**
     * Gets the {@link Rules} that is attached to this properties.
     *
     * @return the rules
     */
    public Rules getRules() {
        return this.rules;
    }

    public WorldConfig getConfig() {
        return this.worldConfig;
    }

    public boolean doesWaterEvaporate() {
        return this.worldConfig.doesWaterEvaporate();
    }

    public void setWaterEvaporates(boolean evaporates) {
        this.worldConfig.setDoesWaterEvaporate(evaporates);
    }

    public boolean allowsPlayerRespawns() {
        return this.worldConfig.allowPlayerRespawns();
    }

    public void setAllowsPlayerRespawns(boolean allow) {
        boolean update = this.worldConfig.allowPlayerRespawns() != allow;
        this.worldConfig.setAllowPlayerRespawns(allow);
        if (update && this.world != null) {
            this.world.enableSpawnArea(allow);
        }
    }

    public int getBuildHeight() {
        return this.worldConfig.getMaxBuildHeight();
    }

    public void setBuildHeight(int buildHeight) {
        this.worldConfig.setMaxBuildHeight(buildHeight);
    }

    long getLastPlayedTime() {
        if (this.world != null) {
            return this.lastPlayed = System.currentTimeMillis();
        }
        return this.lastPlayed;
    }

    void setLastPlayedTime(long time) {
        this.lastPlayed = time;
    }

    public Optional<LanternWorld> getWorld() {
        return Optional.ofNullable(this.world);
    }

    void setWorld(@Nullable LanternWorld world) {
        this.world = world;
        if (this.world != null && world == null) {
            this.lastPlayed = System.currentTimeMillis();
        }
    }

    @Override
    public int getContentVersion() {
        return 0;
    }

    @Override
    public DataContainer toContainer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isEnabled() {
        return this.worldConfig.isWorldEnabled();
    }

    @Override
    public void setEnabled(boolean state) {
        this.worldConfig.setWorldEnabled(state);
    }

    @Override
    public boolean loadOnStartup() {
        return this.worldConfig.loadOnStartup();
    }

    @Override
    public void setLoadOnStartup(boolean state) {
        this.worldConfig.setLoadOnStartup(state);
    }

    @Override
    public boolean doesKeepSpawnLoaded() {
        return this.worldConfig.getKeepSpawnLoaded();
    }

    @Override
    public void setKeepSpawnLoaded(boolean state) {
        this.worldConfig.setKeepSpawnLoaded(state);
    }

    @Override
    public boolean doesGenerateSpawnOnLoad() {
        return this.worldConfig.getGeneration().doesGenerateSpawnOnLoad();
    }

    @Override
    public void setGenerateSpawnOnLoad(boolean state) {
        this.worldConfig.getGeneration().setGenerateSpawnOnLoad(state);
    }

    @Override
    public String getWorldName() {
        return this.name;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public Vector3i getSpawnPosition() {
        return this.spawnPosition;
    }

    @Override
    public void setSpawnPosition(Vector3i position) {
        this.spawnPosition = checkNotNull(position, "position");
        // Generate the spawn are at the new spawn position
        // if the world is present
        boolean keepSpawnLoaded = this.worldConfig.getKeepSpawnLoaded();
        if (keepSpawnLoaded && this.world != null) {
            this.world.enableSpawnArea(true);
        }
    }

    @Override
    public LanternGeneratorType getGeneratorType() {
        return (LanternGeneratorType) this.worldConfig.getGeneration().getGeneratorType();
    }

    @Override
    public void setGeneratorType(GeneratorType generatorType) {
        this.worldConfig.getGeneration().setGeneratorType(generatorType);
    }

    @Override
    public DataContainer getGeneratorSettings() {
        return this.worldConfig.getGeneration().getGeneratorSettings();
    }

    @Override
    public long getSeed() {
        return this.worldConfig.getGeneration().getSeed();
    }

    @Override
    public void setSeed(long seed) {
        this.worldConfig.getGeneration().setSeed(seed);
    }

    @Override
    public long getTotalTime() {
        return this.timeData.getAge();
    }

    @Override
    public long getWorldTime() {
        return this.timeData.getDayTime();
    }

    @Override
    public void setWorldTime(long time) {
        this.timeData.setDayTime(time);
    }

    @Override
    public LanternDimensionType getDimensionType() {
        return this.dimensionType;
    }

    @Override
    public LanternPortalAgentType getPortalAgentType() {
        return this.portalAgentType;
    }

    /**
     * Sets the {@link DimensionType}.
     *
     * @param dimensionType The dimension type
     */
    public void setDimensionType(DimensionType dimensionType) {
        this.dimensionType = (LanternDimensionType<?>) checkNotNull(dimensionType, "dimensionType");
    }

    public void setPortalAgentType(PortalAgentType portalAgentType) {
        this.portalAgentType = (LanternPortalAgentType) checkNotNull(portalAgentType, "portalAgentType");
    }

    @Override
    public boolean isRaining() {
        final Weather weather = this.weatherData.getWeather();
        return ((LanternWeather) weather).getOptions().getOrDefault(WeatherOptions.RAIN_STRENGTH).get() > 0;
    }

    @Override
    public void setRaining(boolean state) {
        LanternWeather weather = this.weatherData.getWeather();
        final boolean raining = weather.getOptions().getOrDefault(WeatherOptions.RAIN_STRENGTH).get() > 0;
        if (raining != state) {
            weather = (LanternWeather) (state ? Weathers.RAIN : Weathers.CLEAR);
            this.weatherData.setWeather(weather);
            this.weatherData.setRemainingDuration(weather.getRandomTicksDuration());
            this.weatherData.setRunningDuration(0);
        }
    }

    @Override
    public int getRainTime() {
        return this.isRaining() ? (int) this.weatherData.getRemainingDuration() : 0;
    }

    @Override
    public void setRainTime(int time) {
        final Weather weather = this.weatherData.getWeather();
        final boolean raining = ((LanternWeather) weather).getOptions().getOrDefault(WeatherOptions.RAIN_STRENGTH).get() > 0;
        if (raining) {
            this.weatherData.setRemainingDuration(time);
        }
    }

    @Override
    public boolean isThundering() {
        final Weather weather = this.weatherData.getWeather();
        return weather == Weathers.THUNDER_STORM;
    }

    @Override
    public void setThundering(boolean state) {
        LanternWeather weather = this.weatherData.getWeather();
        final boolean thunderStorm = weather == Weathers.THUNDER_STORM;
        if (thunderStorm != state) {
            weather = (LanternWeather) (state ? Weathers.THUNDER_STORM : Weathers.CLEAR);
            this.weatherData.setWeather(weather);
            this.weatherData.setRemainingDuration(weather.getRandomTicksDuration());
            this.weatherData.setRunningDuration(0);
        }
    }

    @Override
    public int getThunderTime() {
        return this.isThundering() ? (int) this.weatherData.getRemainingDuration() : 0;
    }

    @Override
    public void setThunderTime(int time) {
        final Weather weather = this.weatherData.getWeather();
        final boolean thunderStorm = weather == Weathers.THUNDER_STORM;
        if (thunderStorm) {
            this.weatherData.setRemainingDuration(time);
        }
    }

    @Override
    public GameMode getGameMode() {
        return this.worldConfig.getGameMode();
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        this.worldConfig.setGameMode(gameMode);
    }

    @Override
    public boolean usesMapFeatures() {
        return this.mapFeatures;
    }

    @Override
    public void setMapFeaturesEnabled(boolean state) {
        this.mapFeatures = state;
    }

    @Override
    public boolean isHardcore() {
        return this.worldConfig.isHardcore();
    }

    @Override
    public void setHardcore(boolean state) {
        this.worldConfig.setHardcore(state);
    }

    @Override
    public boolean areCommandsAllowed() {
        return this.commandsAllowed;
    }

    @Override
    public void setCommandsAllowed(boolean state) {
        this.commandsAllowed = state;
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    /**
     * Sets whether the world properties and world are initialized.
     *
     * @param initialized Is initialized
     */
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    @Override
    public Difficulty getDifficulty() {
        return this.worldConfig.getDifficulty();
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
        checkNotNull(difficulty, "difficulty");
        if (this.getDifficulty() != difficulty && this.world != null) {
            this.world.broadcast(() -> new MessagePlayOutSetDifficulty((LanternDifficulty) difficulty));
        }
        this.worldConfig.setDifficulty(difficulty);
    }

    @Override
    public boolean doesGenerateBonusChest() {
        return this.generateBonusChest;
    }

    public void setGenerateBonusChest(boolean generateBonusChest) {
        this.generateBonusChest = generateBonusChest;
    }

    @Override
    public Optional<String> getGameRule(String gameRule) {
        final Optional<RuleType<?>> optRuleType = RuleType.get(gameRule);
        if (!optRuleType.isPresent()) {
            return Optional.empty();
        }
        //noinspection unchecked
        final Optional<Rule> rule = this.rules.getRule((RuleType) optRuleType.get());
        if (!rule.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(rule.get().getRawValue());
    }

    @Override
    public Map<String, String> getGameRules() {
        final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        for (Map.Entry<RuleType<?>, Rule<?>> entry : this.rules.getRules().entrySet()) {
            builder.put(entry.getKey().getName(), entry.getValue().getRawValue());
        }
        return builder.build();
    }

    @Override
    public void setGameRule(String gameRule, String value) {
        // We cannot know what type a plugin rule would be, so string
        this.rules.getOrCreateRule(RuleType.getOrCreate(gameRule, RuleDataTypes.STRING, "")).setRawValue(value);
    }

    @Override
    public boolean removeGameRule(String gameRule) {
        final Optional<RuleType<?>> type = RuleType.get(gameRule);
        return type.isPresent() && this.rules.removeRule((RuleType) type.get()).isPresent();
    }

    @Override
    public DataContainer getAdditionalProperties() {
        return this.additionalProperties;
    }

    @Override
    public Optional<DataView> getPropertySection(DataQuery path) {
        return this.additionalProperties.getView(path);
    }

    @Override
    public void setPropertySection(DataQuery path, DataView data) {
        this.additionalProperties.set(path, data);
    }

    /**
     * Sets the additional properties {@link DataContainer}.
     *
     * @param additionalProperties The additional properties
     */
    public void setAdditionalProperties(DataContainer additionalProperties) {
        this.additionalProperties = checkNotNull(additionalProperties, "additionalProperties");
    }

    @Override
    public Collection<WorldGeneratorModifier> getGeneratorModifiers() {
        return this.generatorModifiers;
    }

    @Override
    public void setGeneratorModifiers(Collection<WorldGeneratorModifier> modifiers) {
        this.generatorModifiers = ImmutableSet.copyOf(this.generatorModifiers);
        final List<String> genModifiers = this.worldConfig.getGeneration().getGenerationModifiers();
        genModifiers.clear();
        genModifiers.addAll(modifiers.stream().map(CatalogType::getId).collect(Collectors.toList()));
    }

    @Override
    public SerializationBehavior getSerializationBehavior() {
        return this.serializationBehavior;
    }

    @Override
    public void setSerializationBehavior(SerializationBehavior behavior) {
        this.serializationBehavior = checkNotNull(behavior, "behavior");
    }

    @Override
    public Vector3d getWorldBorderCenter() {
        return new Vector3d(this.borderCenterX, 0, this.borderCenterZ);
    }

    public MessagePlayOutWorldBorder createWorldBorderMessage() {
        return new MessagePlayOutWorldBorder.Initialize(this.borderCenterX, this.borderCenterZ, this.borderDiameterStart,
                this.borderDiameterEnd, this.getWorldBorderTimeRemaining(), BOUNDARY, this.borderWarningDistance,
                this.borderWarningTime);
    }

    public void setBorderDiameter(double startDiameter, double endDiameter, long time) {
        checkArgument(startDiameter >= 0, "The start diameter cannot be negative!");
        checkArgument(endDiameter >= 0, "The end diameter cannot be negative!");
        checkArgument(time >= 0, "The duration cannot be negative!");

        // Only shrink or grow if needed
        if (time == 0 || startDiameter == endDiameter) {
            this.borderDiameterStart = endDiameter;
            this.borderDiameterEnd = endDiameter;
            this.setCurrentBorderTime(0);
            if (this.world != null) {
                this.world.broadcast(() -> new MessagePlayOutWorldBorder.UpdateDiameter(endDiameter));
            }
        } else {
            this.borderDiameterStart = startDiameter;
            this.borderDiameterEnd = endDiameter;
            this.setCurrentBorderTime(time);
            if (this.world != null) {
                this.world.broadcast(() -> new MessagePlayOutWorldBorder.UpdateLerpedDiameter(startDiameter,
                        endDiameter, time));
            }
        }
    }

    @Override
    public void setWorldBorderCenter(double x, double z) {
        this.borderCenterX = x;
        this.borderCenterZ = z;

        if (this.world != null) {
            this.world.broadcast(() -> new MessagePlayOutWorldBorder.UpdateCenter(this.borderCenterX,
                    this.borderCenterZ));
        }
    }

    @Override
    public double getWorldBorderDiameter() {
        if (this.borderTimeStart == -1) {
            this.updateCurrentBorderTime();
        }

        if (this.borderDiameterStart != this.borderDiameterEnd) {
            long lerpTime = this.borderTimeEnd - this.borderTimeStart;
            if (lerpTime == 0) {
                return this.borderDiameterStart;
            }

            long elapsedTime = System.currentTimeMillis() - this.borderTimeStart;
            elapsedTime = elapsedTime > lerpTime ? lerpTime : elapsedTime < 0 ? 0 : elapsedTime;

            double d = elapsedTime / lerpTime;
            double diameter;

            if (d == 0.0) {
                diameter = this.borderDiameterStart;
            } else {
                diameter = this.borderDiameterStart + (this.borderDiameterEnd - this.borderDiameterStart) * d;
            }

            this.borderDiameterStart = diameter;
            this.setCurrentBorderTime(lerpTime - elapsedTime);
            return diameter;
        } else {
            return this.borderDiameterStart;
        }
    }

    @Override
    public void setWorldBorderDiameter(double diameter) {
        this.borderDiameterStart = diameter;
    }

    @Override
    public long getWorldBorderTimeRemaining() {
        if (this.borderTimeStart == -1) {
            this.updateCurrentBorderTime();
        }
        return Math.max(this.borderTimeEnd - System.currentTimeMillis(), 0);
    }

    void updateCurrentBorderTime() {
        this.updateCurrentBorderTime(this.borderLerpTime);
    }

    private void setCurrentBorderTime(long time) {
        this.updateCurrentBorderTime(time);
        this.borderLerpTime = time;
    }

    private void updateCurrentBorderTime(long time) {
        this.borderTimeStart = System.currentTimeMillis();
        this.borderTimeEnd = this.borderTimeStart + time;
    }

    @Override
    public void setWorldBorderTimeRemaining(long time) {
        this.setCurrentBorderTime(time);
        if (this.world != null) {
            this.world.broadcast(() -> time == 0 ? new MessagePlayOutWorldBorder.UpdateDiameter(this.borderDiameterEnd) :
                new MessagePlayOutWorldBorder.UpdateLerpedDiameter(this.getWorldBorderDiameter(), this.borderDiameterEnd,
                        this.getWorldBorderTimeRemaining()));
        }
    }

    @Override
    public double getWorldBorderTargetDiameter() {
        return this.borderDiameterEnd;
    }

    @Override
    public void setWorldBorderTargetDiameter(double diameter) {
        this.borderDiameterEnd = diameter;
        if (this.world != null) {
            this.world.broadcast(() -> this.getWorldBorderTimeRemaining() == 0 ? new MessagePlayOutWorldBorder.UpdateDiameter(
                    diameter) : new MessagePlayOutWorldBorder.UpdateLerpedDiameter(this.getWorldBorderDiameter(),
                            diameter, this.getWorldBorderTimeRemaining()));
        }
    }

    @Override
    public double getWorldBorderDamageThreshold() {
        return this.borderDamageThreshold;
    }

    @Override
    public void setWorldBorderDamageThreshold(double distance) {
        this.borderDamageThreshold = distance;
    }

    @Override
    public double getWorldBorderDamageAmount() {
        return this.borderDamage;
    }

    @Override
    public void setWorldBorderDamageAmount(double damage) {
        this.borderDamage = damage;
    }

    @Override
    public int getWorldBorderWarningTime() {
        return this.borderWarningTime;
    }

    @Override
    public void setWorldBorderWarningTime(int time) {
        this.borderWarningTime = time;
        if (this.world != null) {
            this.world.broadcast(() -> new MessagePlayOutWorldBorder.UpdateWarningTime(time));
        }
    }

    @Override
    public int getWorldBorderWarningDistance() {
        return this.borderWarningDistance;
    }

    @Override
    public void setWorldBorderWarningDistance(int distance) {
        this.borderWarningDistance = distance;
        if (this.world != null) {
            this.world.broadcast(() -> new MessagePlayOutWorldBorder.UpdateWarningDistance(distance));
        }
    }

    @Override
    public boolean isPVPEnabled() {
        return this.worldConfig.getPVPEnabled();
    }

    @Override
    public void setPVPEnabled(boolean enabled) {
        this.worldConfig.setPVPEnabled(enabled);
    }

    /**
     * Gets whether the {@link Difficulty} is locked. This is only used in singleplayer.
     *
     * @return Is difficulty locked
     */
    public boolean isDifficultyLocked() {
        return this.difficultyLocked;
    }

    /**
     * Sets whether the {@link Difficulty} is locked. This is only used in singleplayer.
     *
     * @param difficultyLocked Is difficulty locked
     */
    public void setDifficultyLocked(boolean difficultyLocked) {
        this.difficultyLocked = difficultyLocked;
    }

    public WeatherData getWeatherData() {
        return this.weatherData;
    }

    public TimeData getTimeData() {
        return this.timeData;
    }

    public TrackerIdAllocator getTrackerIdAllocator() {
        return this.trackerIdAllocator;
    }
}
