/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetDifficulty;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldBorder;
import org.lanternpowered.server.world.difficulty.LanternDifficulty;
import org.lanternpowered.server.world.dimension.LanternDimensionType;
import org.lanternpowered.server.world.gen.LanternGeneratorType;
import org.lanternpowered.server.world.rules.GameRule;
import org.lanternpowered.server.world.rules.LanternGameRules;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class LanternWorldProperties implements WorldProperties {

    private static final int BOUNDARY = 29999984;

    // The unique id of the world
    final UUID uniqueId;

    // The rules of the world
    final LanternGameRules rules = new LanternGameRules();

    // This is a map added by sponge, not sure what it is supposed to do yet
    final List<UUID> pendingUniqueIds = Lists.newArrayList();

    // The settings that were used to create the properties
    @Nullable final LanternWorldCreationSettings creationSettings;

    // The extra properties
    DataContainer properties;

    // The type of the dimension
    LanternDimensionType<?> dimensionType;

    // The world generator modifiers
    ImmutableSet<WorldGeneratorModifier> generatorModifiers = ImmutableSet.of();

    // The generator type
    LanternGeneratorType generatorType;

    // The generator settings
    DataContainer generatorSettings;

    // The difficulty
    Difficulty difficulty = Difficulties.NORMAL;

    // Whether the difficulty is locked
    boolean difficultyLocked;

    // The game mode
    GameMode gameMode = GameModes.NOT_SET;

    // The name of the world
    String name;

    // The spawn position
    Vector3i spawnPosition = Vector3i.ZERO;

    // Whether the world is initialized
    boolean initialized;
    boolean bonusChestEnabled;
    boolean enabled;
    boolean loadOnStartup;
    boolean keepSpawnLoaded;
    boolean commandsAllowed;
    boolean mapFeatures;
    boolean hardcore;
    boolean thundering;
    boolean raining;

    int rainTime;
    int thunderTime;
    int clearWeatherTime;

    long sizeOnDisk;
    long seed;
    long time;
    long age;

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

    // Custom properties (the ones moved from the dimension instances)
    boolean waterEvaporates;
    boolean allowPlayerRespawns;

    int buildHeight;

    public LanternWorldProperties(UUID uniqueId) {
        this.creationSettings = null;
        this.uniqueId = uniqueId;
    }

    public LanternWorldProperties(LanternWorldCreationSettings creationSettings) {
        this.properties = new MemoryDataContainer();
        this.creationSettings = creationSettings;
        this.keepSpawnLoaded = creationSettings.doesKeepSpawnLoaded();
        this.commandsAllowed = creationSettings.commandsAllowed();
        this.hardcore = creationSettings.isHardcore();
        this.dimensionType = creationSettings.getDimensionType();
        this.generatorType = (LanternGeneratorType) creationSettings.getGeneratorType();
        this.generatorSettings = creationSettings.getGeneratorSettings();
        this.setGeneratorModifiers(creationSettings.getGeneratorModifiers());
        this.waterEvaporates = creationSettings.waterEvaporates();
        this.bonusChestEnabled = creationSettings.bonusChestEnabled();
        this.buildHeight = creationSettings.getBuildHeight();
        this.mapFeatures = creationSettings.usesMapFeatures();
        this.gameMode = creationSettings.getGameMode();
        this.enabled = creationSettings.isEnabled();
        this.name = creationSettings.getWorldName();
        this.seed = creationSettings.getSeed();
        this.allowPlayerRespawns = creationSettings.allowPlayerRespawns();
        this.uniqueId = UUID.randomUUID();
    }

    /**
     * Sets whether the world is initialized.
     */
    public void setInitialized() {
        this.initialized = true;
    }

    public boolean doesWaterEvaporate() {
        return this.waterEvaporates;
    }

    public void setWaterEvaporates(boolean evaporates) {
        this.waterEvaporates = evaporates;
    }

    public boolean allowsPlayerRespawns() {
        return this.allowPlayerRespawns;
    }

    public void setAllowsPlayerRespawns(boolean allow) {
        this.allowPlayerRespawns = allow;
        if (this.world != null) {
            this.world.enableSpawnArea(allow);
        }
    }

    public int getBuildHeight() {
        return this.buildHeight;
    }

    public void setBuildHeight(int buildHeight) {
        this.buildHeight = buildHeight;
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

    @Nullable
    LanternWorld getWorld() {
        return this.world;
    }

    void setWorld(@Nullable LanternWorld world) {
        this.world = world;
        if (this.world != null && world == null) {
            this.lastPlayed = System.currentTimeMillis();
        }
    }

    @Override
    public DataContainer toContainer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean state) {
        this.enabled = state;
    }

    @Override
    public boolean loadOnStartup() {
        return this.loadOnStartup;
    }

    @Override
    public void setLoadOnStartup(boolean state) {
        this.loadOnStartup = state;
    }

    @Override
    public boolean doesKeepSpawnLoaded() {
        return this.keepSpawnLoaded;
    }

    @Override
    public void setKeepSpawnLoaded(boolean state) {
        this.keepSpawnLoaded = state;
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
        if (this.world != null) {
            this.world.enableSpawnArea(this.keepSpawnLoaded);
        }
    }

    @Override
    public LanternGeneratorType getGeneratorType() {
        return this.generatorType;
    }

    @Override
    public void setGeneratorType(GeneratorType type) {
        this.generatorType = (LanternGeneratorType) checkNotNull(type, "type");
    }

    @Override
    public long getSeed() {
        return this.seed;
    }

    @Override
    public long getTotalTime() {
        return this.age;
    }

    @Override
    public long getWorldTime() {
        return this.time;
    }

    @Override
    public void setWorldTime(long time) {
        this.time = time;
    }

    @Override
    public DimensionType getDimensionType() {
        return this.dimensionType;
    }

    @Override
    public boolean isRaining() {
        return this.raining;
    }

    @Override
    public void setRaining(boolean state) {
        this.raining = state;
        if (this.world != null && this.world.weatherUniverse != null) {
            this.world.weatherUniverse.setRaining(state);
        }
    }

    @Override
    public int getRainTime() {
        return this.rainTime;
    }

    @Override
    public void setRainTime(int time) {
        this.rainTime = time;
        if (this.world != null && this.world.weatherUniverse != null) {
            this.world.weatherUniverse.setRainTime(time);
        }
    }

    @Override
    public boolean isThundering() {
        return this.thundering;
    }

    @Override
    public void setThundering(boolean state) {
        this.thundering = state;
        if (this.world != null && this.world.weatherUniverse != null) {
            this.world.weatherUniverse.setThundering(state);
        }
    }

    @Override
    public int getThunderTime() {
        return this.thunderTime;
    }

    @Override
    public void setThunderTime(int time) {
        this.thunderTime = time;
        if (this.world != null && this.world.weatherUniverse != null) {
            this.world.weatherUniverse.setThunderTime(time);
        }
    }

    @Override
    public GameMode getGameMode() {
        return this.gameMode;
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        this.gameMode = checkNotNull(gameMode, "gameMode");
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
        return this.hardcore;
    }

    @Override
    public void setHardcore(boolean state) {
        this.hardcore = state;
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

    @Override
    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
        checkNotNull(difficulty, "difficulty");
        if (this.difficulty != difficulty) {
            this.forPlayersIfPresent(players -> {
                MessagePlayOutSetDifficulty message = new MessagePlayOutSetDifficulty((LanternDifficulty) difficulty);
                players.forEach(player -> player.getConnection().send(message));
            });
        }
        this.difficulty = difficulty;
    }

    @Override
    public Optional<String> getGameRule(String gameRule) {
        Optional<GameRule> rule = this.rules.getRule(gameRule);
        if (!rule.isPresent()) {
            return Optional.empty();
        }
        return rule.get().asString();
    }

    @Override
    public Map<String, String> getGameRules() {
        return this.rules.getValues();
    }

    @Override
    public void setGameRule(String gameRule, String value) {
        this.rules.newRule(gameRule).set(value);
    }

    @Override
    public DataContainer getAdditionalProperties() {
        return this.properties;
    }

    @Override
    public Optional<DataView> getPropertySection(DataQuery path) {
        return this.properties.getView(path);
    }

    @Override
    public void setPropertySection(DataQuery path, DataView data) {
        this.properties.set(path, data);
    }

    @Override
    public Collection<WorldGeneratorModifier> getGeneratorModifiers() {
        return this.generatorModifiers;
    }

    @Override
    public void setGeneratorModifiers(Collection<WorldGeneratorModifier> modifiers) {
        this.generatorModifiers = ImmutableSet.copyOf(this.generatorModifiers);
    }

    @Override
    public DataContainer getGeneratorSettings() {
        if (this.generatorSettings == null) {
            this.generatorSettings = this.generatorType.getGeneratorSettings();
        }
        return this.generatorSettings;
    }

    @Override
    public Vector3d getWorldBorderCenter() {
        return new Vector3d(this.borderCenterX, 0, this.borderCenterZ);
    }

    public MessagePlayOutWorldBorder createWorldBorderMessage() {
        return MessagePlayOutWorldBorder.initialize(this.borderCenterX, this.borderCenterZ, this.borderDiameterStart,
                this.borderDiameterEnd, this.getWorldBorderTimeRemaining(), BOUNDARY, this.borderWarningDistance,
                this.borderWarningTime);
    }

    void forPlayersIfPresent(Consumer<List<LanternPlayer>> consumer) {
        if (this.world != null) {
            List<LanternPlayer> players = this.world.getPlayers();
            if (!players.isEmpty()) {
                consumer.accept(players);
            }
        }
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
            this.forPlayersIfPresent(players -> {
                MessagePlayOutWorldBorder message = MessagePlayOutWorldBorder.setSize(endDiameter);
                players.forEach(player -> player.getConnection().send(message));
            });
        } else {
            this.borderDiameterStart = startDiameter;
            this.borderDiameterEnd = endDiameter;
            this.setCurrentBorderTime(time);
            this.forPlayersIfPresent(players -> {
                MessagePlayOutWorldBorder message = MessagePlayOutWorldBorder.lerpSize(startDiameter,
                        endDiameter, time);
                players.forEach(player -> player.getConnection().send(message));
            });
        }
    }

    @Override
    public void setWorldBorderCenter(double x, double z) {
        this.borderCenterX = x;
        this.borderCenterZ = z;

        this.forPlayersIfPresent(players -> {
            MessagePlayOutWorldBorder message = MessagePlayOutWorldBorder.setCenter(this.borderCenterX,
                    this.borderCenterZ);
            players.forEach(player -> player.getConnection().send(message));
        });
    }

    @Override
    public double getWorldBorderDiameter() {
        if (this.borderTimeStart == -1) {
            this.updateCurrentBorderTime();
        }
        if (this.borderDiameterStart != this.borderDiameterEnd) {
            double d = Math.max(this.borderTimeEnd - System.currentTimeMillis(), 0) / (this.borderTimeEnd - this.borderTimeStart);

            if (d == 0d) {
                return this.borderDiameterStart;
            } else {
                return this.borderDiameterStart + (this.borderDiameterEnd - this.borderDiameterStart) * d;
            }
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
        if (time == 0) {
            this.forPlayersIfPresent(players -> {
                MessagePlayOutWorldBorder message = MessagePlayOutWorldBorder.setSize(this.borderDiameterEnd);
                players.forEach(player -> player.getConnection().send(message));
            });
        } else {
            this.forPlayersIfPresent(players -> {
                MessagePlayOutWorldBorder message = MessagePlayOutWorldBorder.lerpSize(this.getWorldBorderDiameter(),
                        this.borderDiameterEnd, this.getWorldBorderTimeRemaining());
                players.forEach(player -> player.getConnection().send(message));
            });
        }
    }

    @Override
    public double getWorldBorderTargetDiameter() {
        return this.borderDiameterEnd;
    }

    @Override
    public void setWorldBorderTargetDiameter(double diameter) {
        this.borderDiameterEnd = diameter;
        if (this.getWorldBorderTimeRemaining() == 0) {
            this.forPlayersIfPresent(players -> {
                MessagePlayOutWorldBorder message = MessagePlayOutWorldBorder.setSize(diameter);
                players.forEach(player -> player.getConnection().send(message));
            });
        } else {
            this.forPlayersIfPresent(players -> {
                MessagePlayOutWorldBorder message = MessagePlayOutWorldBorder.lerpSize(this.getWorldBorderDiameter(),
                        diameter, this.getWorldBorderTimeRemaining());
                players.forEach(player -> player.getConnection().send(message));
            });
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
        this.forPlayersIfPresent(players -> {
            MessagePlayOutWorldBorder message = MessagePlayOutWorldBorder.setWarningTime(time);
            players.forEach(player -> player.getConnection().send(message));
        });
    }

    @Override
    public int getWorldBorderWarningDistance() {
        return this.borderWarningDistance;
    }

    @Override
    public void setWorldBorderWarningDistance(int distance) {
        this.borderWarningDistance = distance;
        this.forPlayersIfPresent(players -> {
            MessagePlayOutWorldBorder message = MessagePlayOutWorldBorder.setWarningBlocks(distance);
            players.forEach(player -> player.getConnection().send(message));
        });
    }
}
