package org.lanternpowered.server.world;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.lanternpowered.server.world.rules.GameRule;
import org.lanternpowered.server.world.rules.GameRules;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.entity.player.gamemode.GameMode;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Optional;

public class LanternWorldProperties implements WorldProperties {

    private String name;
    private UUID uniqueId;
    private Vector3i spawnPosition;
    private Difficulty difficulty;
    private GameRules rules;

    private boolean enabled;
    private boolean loadOnStartup;
    private boolean keepSpawnLoaded;
    private boolean commandsAllowed;
    private boolean mapFeatures;
    private boolean hardcore;

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
    }

    @Override
    public GeneratorType getGeneratorType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setGeneratorType(GeneratorType type) {
        // TODO Auto-generated method stub

    }

    @Override
    public long getSeed() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getTotalTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getWorldTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setWorldTime(long time) {
        // TODO Auto-generated method stub

    }

    @Override
    public DimensionType getDimensionType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isRaining() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setRaining(boolean state) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getRainTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setRainTime(int time) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isThundering() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setThundering(boolean state) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getThunderTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setThunderTime(int time) {
        // TODO Auto-generated method stub

    }

    @Override
    public GameMode getGameMode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setGameMode(GameMode gamemode) {
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = checkNotNull(difficulty, "difficulty");
    }

    @Override
    public Optional<String> getGameRule(String gameRule) {
        Optional<GameRule> rule = this.rules.getRule(gameRule);
        if (!rule.isPresent()) {
            return Optional.absent();
        }
        return Optional.of(rule.get().stringValue());
    }

    @Override
    public Map<String, String> getGameRules() {
        return this.rules.getValues();
    }

    @Override
    public void setGameRule(String gameRule, String value) {
        this.rules.newRule(checkNotNull(gameRule, "gameRule")).setValue(value);
    }

    @Override
    public DataContainer getAdditionalProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<DataView> getPropertySection(DataQuery path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPropertySection(DataQuery path, DataView data) {
        // TODO Auto-generated method stub

    }

    @Override
    public Collection<WorldGeneratorModifier> getGeneratorModifiers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setGeneratorModifiers(Collection<WorldGeneratorModifier> modifiers) {
        // TODO Auto-generated method stub

    }

    @Override
    public DataContainer getGeneratorSettings() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Vector3d getWorldBorderCenter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setWorldBorderCenter(double x, double z) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public double getWorldBorderDiameter() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setWorldBorderDiameter(double diameter) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public long getWorldBorderTimeRemaining() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setWorldBorderTimeRemaining(long time) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public double getWorldBorderTargetDiameter() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setWorldBorderTargetDiameter(double diameter) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public double getWorldBorderDamageThreshold() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setWorldBorderDamageThreshold(double distance) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public double getWorldBorderDamageAmount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setWorldBorderDamageAmount(double damage) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getWorldBorderWarningTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setWorldBorderWarningTime(int time) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getWorldBorderWarningDistance() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setWorldBorderWarningDistance(int distance) {
        // TODO Auto-generated method stub
        
    }

}
