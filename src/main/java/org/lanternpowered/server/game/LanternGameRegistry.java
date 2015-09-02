package org.lanternpowered.server.game;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.lanternpowered.server.attribute.LanternAttributeBuilder;
import org.lanternpowered.server.attribute.LanternAttributeCalculator;
import org.lanternpowered.server.attribute.LanternAttributeModifierBuilder;
import org.lanternpowered.server.block.LanternBlockRegistry;
import org.lanternpowered.server.catalog.CatalogTypeRegistry;
import org.lanternpowered.server.catalog.SimpleCatalogTypeRegistry;
import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.lanternpowered.server.item.LanternItemRegistry;
import org.lanternpowered.server.resourcepack.LanternResourcePackFactory;
import org.lanternpowered.server.status.LanternFavicon;
import org.lanternpowered.server.text.translation.LanternTranslationManager;
import org.lanternpowered.server.text.translation.TranslationManager;
import org.lanternpowered.server.world.LanternWorldBuilder;
import org.lanternpowered.server.world.biome.LanternBiomeRegistry;
import org.lanternpowered.server.world.difficulty.LanternDifficulty;
import org.lanternpowered.server.world.extent.LanternExtentBufferFactory;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.GameDictionary;
import org.spongepowered.api.GameProfile;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.attribute.Attribute;
import org.spongepowered.api.attribute.AttributeBuilder;
import org.spongepowered.api.attribute.AttributeCalculator;
import org.spongepowered.api.attribute.AttributeModifierBuilder;
import org.spongepowered.api.block.BlockSnapshotBuilder;
import org.spongepowered.api.block.BlockStateBuilder;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.ImmutableDataRegistry;
import org.spongepowered.api.data.manipulator.DataManipulatorRegistry;
import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.data.type.Profession;
import org.spongepowered.api.data.value.ValueBuilder;
import org.spongepowered.api.effect.particle.ParticleEffectBuilder;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.entity.EntitySnapshotBuilder;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.cause.entity.damage.source.BlockDamageSourceBuilder;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSourceBuilder;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSourceBuilder;
import org.spongepowered.api.event.cause.entity.damage.source.FallingBlockDamageSourceBuilder;
import org.spongepowered.api.event.cause.entity.damage.source.ProjectileDamageSourceBuilder;
import org.spongepowered.api.event.cause.entity.spawn.BlockSpawnCauseBuilder;
import org.spongepowered.api.event.cause.entity.spawn.BreedingSpawnCauseBuilder;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCauseBuilder;
import org.spongepowered.api.event.cause.entity.spawn.MobSpawnerSpawnCauseBuilder;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCauseBuilder;
import org.spongepowered.api.event.cause.entity.spawn.WeatherSpawnCauseBuilder;
import org.spongepowered.api.item.FireworkEffectBuilder;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStackBuilder;
import org.spongepowered.api.item.merchant.TradeOfferBuilder;
import org.spongepowered.api.item.recipe.RecipeRegistry;
import org.spongepowered.api.potion.PotionEffectBuilder;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.resourcepack.ResourcePackFactory;
import org.spongepowered.api.scoreboard.ScoreboardBuilder;
import org.spongepowered.api.scoreboard.TeamBuilder;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlot;
import org.spongepowered.api.scoreboard.objective.ObjectiveBuilder;
import org.spongepowered.api.statistic.BlockStatistic;
import org.spongepowered.api.statistic.EntityStatistic;
import org.spongepowered.api.statistic.ItemStatistic;
import org.spongepowered.api.statistic.Statistic;
import org.spongepowered.api.statistic.StatisticBuilder;
import org.spongepowered.api.statistic.StatisticBuilder.BlockStatisticBuilder;
import org.spongepowered.api.statistic.StatisticBuilder.EntityStatisticBuilder;
import org.spongepowered.api.statistic.StatisticBuilder.ItemStatisticBuilder;
import org.spongepowered.api.statistic.StatisticBuilder.TeamStatisticBuilder;
import org.spongepowered.api.statistic.StatisticGroup;
import org.spongepowered.api.statistic.TeamStatistic;
import org.spongepowered.api.statistic.achievement.AchievementBuilder;
import org.spongepowered.api.status.Favicon;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.rotation.Rotation;
import org.spongepowered.api.world.WorldBuilder;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.explosion.ExplosionBuilder;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.api.world.extent.ExtentBufferFactory;
import org.spongepowered.api.world.gamerule.DefaultGameRules;
import org.spongepowered.api.world.gen.PopulatorFactory;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class LanternGameRegistry implements GameRegistry {

    private final LanternGame game;
    private final Set<String> defaultGameRules;
    private final LanternTranslationManager translationManager = new LanternTranslationManager();
    private final LanternGameDictionary gameDictionary = new LanternGameDictionary();
    private final LanternResourcePackFactory resourcePackFactory = new LanternResourcePackFactory();
    private final LanternAttributeCalculator attributeCalculator = new LanternAttributeCalculator();
    private final LanternBiomeRegistry biomeRegistry = new LanternBiomeRegistry();
    private final LanternBlockRegistry blockRegistry = new LanternBlockRegistry();
    private final LanternItemRegistry itemRegistry = new LanternItemRegistry();
    private final CatalogTypeRegistry<Difficulty> difficultyRegistry = new SimpleCatalogTypeRegistry<Difficulty>();
    private final CatalogTypeRegistry<GameMode> gameModeRegistry = new SimpleCatalogTypeRegistry<GameMode>();
    private final CatalogTypeRegistry<Attribute> attributeRegistry = new SimpleCatalogTypeRegistry<Attribute>();
    private final CatalogTypeRegistry<WorldGeneratorModifier> worldGeneratorModifierRegistry =
            new SimpleCatalogTypeRegistry<WorldGeneratorModifier>();
    private final Map<Class<?>, CatalogTypeRegistry<?>> catalogTypeRegistries = ImmutableMap.<Class<?>, CatalogTypeRegistry<?>>builder()
            .put(Attribute.class, this.attributeRegistry)
            .put(BiomeType.class, this.biomeRegistry)
            .put(BlockType.class, this.blockRegistry)
            .put(ItemType.class, this.itemRegistry)
            .put(WorldGeneratorModifier.class, this.worldGeneratorModifierRegistry)
            .build();

    {
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        for (Field field : DefaultGameRules.class.getFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType() == String.class) {
                try {
                    builder.add((String) field.get(null));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        this.defaultGameRules = builder.build();
    }

    public LanternGameRegistry(LanternGame game) {
        this.game = game;
        this.registerGameObjects();
    }

    private void registerGameObjects() {
        this.registerDifficulties();
        this.registerGameModes();
    }

    private void registerDifficulties() {
        this.difficultyRegistry.register(new LanternDifficulty("peaceful", 0));
        this.difficultyRegistry.register(new LanternDifficulty("easy", 1));
        this.difficultyRegistry.register(new LanternDifficulty("normal", 2));
        this.difficultyRegistry.register(new LanternDifficulty("hard", 3));
        RegistryHelper.mapFields(Difficulties.class, key -> this.difficultyRegistry.get(key.toLowerCase()).get());
    }

    private void registerGameModes() {
        this.gameModeRegistry.register(new LanternGameMode("not_set", -1));
        this.gameModeRegistry.register(new LanternGameMode("survival", 0));
        this.gameModeRegistry.register(new LanternGameMode("creative", 1));
        this.gameModeRegistry.register(new LanternGameMode("adventure", 2));
        this.gameModeRegistry.register(new LanternGameMode("spectator", 3));
        RegistryHelper.mapFields(GameModes.class, key -> this.gameModeRegistry.get(key.toLowerCase()).get());
    }

    /**
     * Gets the {@link CatalogTypeRegistry<WorldGeneratorModifier>}.
     * 
     * @return the world generator modifier registry
     */
    public CatalogTypeRegistry<WorldGeneratorModifier> getWorldGeneratorModifierRegistry() {
        return this.worldGeneratorModifierRegistry;
    }

    /**
     * Gets the {@link CatalogTypeRegistry<Attribute>}.
     * 
     * @return the attribute registry
     */
    public CatalogTypeRegistry<Attribute> getAttributeRegistry() {
        return this.attributeRegistry;
    }

    /**
     * Gets the {@link LanternBlockRegistry}.
     * 
     * @return the block registry
     */
    public LanternBlockRegistry getBlockRegistry() {
        return this.blockRegistry;
    }

    /**
     * Gets the {@link LanternItemRegistry}.
     * 
     * @return the item registry
     */
    public LanternItemRegistry getItemRegistry() {
        return this.itemRegistry;
    }

    /**
     * Gets the {@link LanternBiomeRegistry}.
     * 
     * @return the biome registry
     */
    public LanternBiomeRegistry getBiomeRegistry() {
        return this.biomeRegistry;
    }

    /**
     * Gets the {@link ResourcePackFactory}.
     * 
     * @return the resource pack factory
     */
    public ResourcePackFactory getResourcePackFactory() {
        return this.resourcePackFactory;
    }

    /**
     * Gets the {@link TranslationManager}.
     * 
     * @return the translation manager
     */
    public TranslationManager getTranslationManager() {
        return this.translationManager;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CatalogType> Optional<T> getType(Class<T> typeClass, String id) {
        if (this.catalogTypeRegistries.containsKey(typeClass)) {
            return (Optional<T>) this.catalogTypeRegistries.get(typeClass).get(id);
        }
        return Optional.absent();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CatalogType> Collection<T> getAllOf(Class<T> typeClass) {
        if (this.catalogTypeRegistries.containsKey(typeClass)) {
            return (Collection<T>) this.catalogTypeRegistries.get(typeClass).getAll();
        }
        return ImmutableList.of();
    }

    @Override
    public AttributeCalculator getAttributeCalculator() {
        return this.attributeCalculator;
    }

    @Override
    public Collection<Career> getCareers(Profession profession) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<String> getDefaultGameRules() {
        return this.defaultGameRules;
    }

    @Override
    public Optional<EntityStatistic> getEntityStatistic(StatisticGroup statisticGroup, EntityType entityType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStatistic> getItemStatistic(StatisticGroup statisticGroup, ItemType itemType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<BlockStatistic> getBlockStatistic(StatisticGroup statisticGroup, BlockType blockType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<TeamStatistic> getTeamStatistic(StatisticGroup statisticGroup, TextColor teamColor) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Statistic> getStatistics(StatisticGroup statisticGroup) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void registerStatistic(Statistic stat) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Optional<Rotation> getRotationFromDegree(int degrees) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GameProfile createGameProfile(UUID uuid, String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Favicon loadFavicon(String raw) throws IOException {
        return LanternFavicon.load(raw);
    }

    @Override
    public Favicon loadFavicon(File file) throws IOException {
        return LanternFavicon.load(file);
    }

    @Override
    public Favicon loadFavicon(URL url) throws IOException {
        return LanternFavicon.load(url);
    }

    @Override
    public Favicon loadFavicon(InputStream in) throws IOException {
        return LanternFavicon.load(in);
    }

    @Override
    public Favicon loadFavicon(BufferedImage image) throws IOException {
        return LanternFavicon.load(image);
    }

    @Override
    public GameDictionary getGameDictionary() {
        return this.gameDictionary;
    }

    @Override
    public RecipeRegistry getRecipeRegistry() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ImmutableDataRegistry getImmutableDataRegistry() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * This method should be renamed in the sponge api,
     * this can cause collisions.
     */
    @Override
    public Optional<ResourcePack> getById(String id) {
        return this.resourcePackFactory.getIfPresent(id);
    }

    @Override
    public Optional<DisplaySlot> getDisplaySlotForColor(TextColor color) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void registerWorldGeneratorModifier(WorldGeneratorModifier modifier) {
        this.worldGeneratorModifierRegistry.register(modifier);
    }

    @Override
    public PopulatorFactory getPopulatorFactory() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Translation> getTranslationById(String id) {
        return this.translationManager.getIfPresent(id);
    }

    @Override
    public <T> Optional<T> createBuilderOfType(Class<T> builderClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ItemStackBuilder createItemBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TradeOfferBuilder createTradeOfferBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FireworkEffectBuilder createFireworkEffectBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PotionEffectBuilder createPotionEffectBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ObjectiveBuilder createObjectiveBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TeamBuilder createTeamBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ScoreboardBuilder createScoreboardBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatisticBuilder createStatisticBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EntityStatisticBuilder createEntityStatisticBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockStatisticBuilder createBlockStatisticBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ItemStatisticBuilder createItemStatisticBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TeamStatisticBuilder createTeamStatisticBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AchievementBuilder createAchievementBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AttributeModifierBuilder createAttributeModifierBuilder() {
        return new LanternAttributeModifierBuilder();
    }

    @Override
    public AttributeBuilder createAttributeBuilder() {
        return new LanternAttributeBuilder(this.attributeRegistry);
    }

    @Override
    public WorldBuilder createWorldBuilder() {
        return new LanternWorldBuilder(this.game);
    }

    @Override
    public ExplosionBuilder createExplosionBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ValueBuilder createValueBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ParticleEffectBuilder createParticleEffectBuilder(ParticleType particle) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataManipulatorRegistry getManipulatorRegistry() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E extends Extent> Transform<E> createTransform() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E extends Extent> Transform<E> createTransform(E extent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ExtentBufferFactory getExtentBufferFactory() {
        return LanternExtentBufferFactory.INSTANCE;
    }

    @Override
    public BlockStateBuilder createBlockStateBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockSnapshotBuilder createBlockSnapshotBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EntitySnapshotBuilder createEntitySnapshotBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockDamageSourceBuilder createBlockDamageSourceBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DamageSourceBuilder createDamageSourceBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EntityDamageSourceBuilder createEntityDamageSourceBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FallingBlockDamageSourceBuilder createFallingBlockDamageSourceBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ProjectileDamageSourceBuilder createProjectileDamageSourceBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SpawnCauseBuilder createSpawnCauseBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockSpawnCauseBuilder createBlockSpawnCauseBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EntitySpawnCauseBuilder createEntitySpawnCauseBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BreedingSpawnCauseBuilder createBreedingSpawnCauseBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MobSpawnerSpawnCauseBuilder createMobSpawnerSpawnCauseBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public WeatherSpawnCauseBuilder createWeatherSpawnCauseBuilder() {
        // TODO Auto-generated method stub
        return null;
    }
}
