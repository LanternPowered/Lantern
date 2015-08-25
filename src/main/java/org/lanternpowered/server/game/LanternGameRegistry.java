package org.lanternpowered.server.game;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import org.lanternpowered.server.attribute.LanternAttributeCalculator;
import org.lanternpowered.server.resourcepack.LanternResourcePackFactory;
import org.lanternpowered.server.status.LanternFavicon;
import org.lanternpowered.server.text.translation.LanternTranslationManager;
import org.lanternpowered.server.text.translation.TranslationManager;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.GameDictionary;
import org.spongepowered.api.GameProfile;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.attribute.AttributeBuilder;
import org.spongepowered.api.attribute.AttributeCalculator;
import org.spongepowered.api.attribute.AttributeModifierBuilder;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.ImmutableDataRegistry;
import org.spongepowered.api.data.manipulator.DataManipulatorRegistry;
import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.data.type.Profession;
import org.spongepowered.api.data.value.ValueBuilder;
import org.spongepowered.api.effect.particle.ParticleEffectBuilder;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.Transform;
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
import org.spongepowered.api.world.explosion.ExplosionBuilder;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.api.world.extent.ExtentBufferFactory;
import org.spongepowered.api.world.gamerule.DefaultGameRules;
import org.spongepowered.api.world.gen.PopulatorFactory;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

public class LanternGameRegistry implements GameRegistry {

    private final Set<String> defaultGameRules;
    private final LanternTranslationManager translationManager = new LanternTranslationManager();
    private final LanternGameDictionary gameDictionary = new LanternGameDictionary();
    private final LanternResourcePackFactory resourcePackFactory = new LanternResourcePackFactory();
    private final LanternAttributeCalculator attributeCalculator = new LanternAttributeCalculator();

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

    @Override
    public <T extends CatalogType> Optional<T> getType(Class<T> typeClass, String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends CatalogType> Collection<T> getAllOf(Class<T> typeClass) {
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AttributeBuilder createAttributeBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public WorldBuilder createWorldBuilder() {
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
    }

}
