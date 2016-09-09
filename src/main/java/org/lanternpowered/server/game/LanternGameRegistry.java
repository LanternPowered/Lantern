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
package org.lanternpowered.server.game;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.lanternpowered.server.attribute.LanternAttribute;
import org.lanternpowered.server.attribute.LanternAttributeBuilder;
import org.lanternpowered.server.attribute.LanternAttributeCalculator;
import org.lanternpowered.server.attribute.LanternOperation;
import org.lanternpowered.server.block.LanternBlockSnapshotBuilder;
import org.lanternpowered.server.block.LanternBlockStateBuilder;
import org.lanternpowered.server.bossbar.LanternBossBarBuilder;
import org.lanternpowered.server.cause.entity.damage.source.LanternBlockDamageSourceBuilder;
import org.lanternpowered.server.cause.entity.damage.source.LanternDamageSourceBuilder;
import org.lanternpowered.server.cause.entity.damage.source.LanternEntityDamageSourceBuilder;
import org.lanternpowered.server.cause.entity.damage.source.LanternFallingBlockDamageSourceBuilder;
import org.lanternpowered.server.cause.entity.damage.source.LanternIndirectEntityDamageSourceBuilder;
import org.lanternpowered.server.cause.entity.spawn.LanternBlockSpawnCauseBuilder;
import org.lanternpowered.server.cause.entity.spawn.LanternBreedingSpawnCauseBuilder;
import org.lanternpowered.server.cause.entity.spawn.LanternEntitySpawnCauseBuilder;
import org.lanternpowered.server.cause.entity.spawn.LanternMobSpawnerSpawnCauseBuilder;
import org.lanternpowered.server.cause.entity.spawn.LanternSpawnCauseBuilder;
import org.lanternpowered.server.cause.entity.spawn.LanternWeatherSpawnCauseBuilder;
import org.lanternpowered.server.cause.entity.teleport.LanternEntityTeleportCauseBuilder;
import org.lanternpowered.server.cause.entity.teleport.LanternPortalTeleportCauseBuilder;
import org.lanternpowered.server.cause.entity.teleport.LanternTeleportCauseBuilder;
import org.lanternpowered.server.config.user.ban.BanBuilder;
import org.lanternpowered.server.data.DataRegistrar;
import org.lanternpowered.server.data.type.LanternBigMushroomType;
import org.lanternpowered.server.data.type.LanternBrickType;
import org.lanternpowered.server.data.type.LanternComparatorType;
import org.lanternpowered.server.data.type.LanternDirtType;
import org.lanternpowered.server.data.type.LanternDisguisedBlockType;
import org.lanternpowered.server.data.type.LanternDoorHalf;
import org.lanternpowered.server.data.type.LanternDoublePlantType;
import org.lanternpowered.server.data.type.LanternHinge;
import org.lanternpowered.server.data.type.LanternLogAxis;
import org.lanternpowered.server.data.type.LanternPistonType;
import org.lanternpowered.server.data.type.LanternPlantType;
import org.lanternpowered.server.data.type.LanternPortionType;
import org.lanternpowered.server.data.type.LanternPrismarineType;
import org.lanternpowered.server.data.type.LanternQuartzType;
import org.lanternpowered.server.data.type.LanternSandType;
import org.lanternpowered.server.data.type.LanternSandstoneType;
import org.lanternpowered.server.data.type.LanternShrubType;
import org.lanternpowered.server.data.type.LanternSlabType;
import org.lanternpowered.server.data.type.LanternStoneType;
import org.lanternpowered.server.data.type.LanternTreeType;
import org.lanternpowered.server.data.type.LanternWallType;
import org.lanternpowered.server.data.value.LanternValueFactory;
import org.lanternpowered.server.effect.particle.LanternParticleEffectBuilder;
import org.lanternpowered.server.effect.potion.LanternPotionEffectBuilder;
import org.lanternpowered.server.entity.living.player.tab.LanternTabListEntryBuilder;
import org.lanternpowered.server.game.registry.CatalogMappingData;
import org.lanternpowered.server.game.registry.CatalogMappingDataHolder;
import org.lanternpowered.server.game.registry.EarlyRegistration;
import org.lanternpowered.server.game.registry.EnumValueRegistryModule;
import org.lanternpowered.server.game.registry.factory.ResourcePackFactoryModule;
import org.lanternpowered.server.game.registry.type.attribute.AttributeOperationRegistryModule;
import org.lanternpowered.server.game.registry.type.attribute.AttributeRegistryModule;
import org.lanternpowered.server.game.registry.type.attribute.AttributeTargetRegistryModule;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.game.registry.type.block.BlockStateRegistryModule;
import org.lanternpowered.server.game.registry.type.bossbar.BossBarColorRegistryModule;
import org.lanternpowered.server.game.registry.type.bossbar.BossBarOverlayRegistryModule;
import org.lanternpowered.server.game.registry.type.cause.DamageTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.cause.DismountTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.cause.SpawnTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.cause.TeleportTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.ArtRegistryModule;
import org.lanternpowered.server.game.registry.type.data.CareerRegistryModule;
import org.lanternpowered.server.game.registry.type.data.HandTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.HorseColorRegistryModule;
import org.lanternpowered.server.game.registry.type.data.HorseStyleRegistryModule;
import org.lanternpowered.server.game.registry.type.data.InstrumentTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.KeyRegistryModule;
import org.lanternpowered.server.game.registry.type.data.NotePitchRegistryModule;
import org.lanternpowered.server.game.registry.type.data.OcelotTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.ProfessionRegistryModule;
import org.lanternpowered.server.game.registry.type.data.RabbitTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.SkinPartRegistryModule;
import org.lanternpowered.server.game.registry.type.data.ToolTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.persistence.DataFormatRegistryModule;
import org.lanternpowered.server.game.registry.type.economy.TransactionTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.effect.ParticleTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.effect.PotionEffectTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.effect.SoundCategoryRegistryModule;
import org.lanternpowered.server.game.registry.type.effect.SoundTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.entity.player.GameModeRegistryModule;
import org.lanternpowered.server.game.registry.type.item.ItemRegistryModule;
import org.lanternpowered.server.game.registry.type.item.inventory.equipment.EquipmentTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.scoreboard.CollisionRuleRegistryModule;
import org.lanternpowered.server.game.registry.type.scoreboard.CriterionRegistryModule;
import org.lanternpowered.server.game.registry.type.scoreboard.DisplaySlotRegistryModule;
import org.lanternpowered.server.game.registry.type.scoreboard.ObjectiveDisplayModeRegistryModule;
import org.lanternpowered.server.game.registry.type.scoreboard.VisibilityRegistryModule;
import org.lanternpowered.server.game.registry.type.text.ArgumentTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.text.ChatTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.text.ChatVisibilityRegistryModule;
import org.lanternpowered.server.game.registry.type.text.LocaleRegistryModule;
import org.lanternpowered.server.game.registry.type.text.SelectorFactoryRegistryModule;
import org.lanternpowered.server.game.registry.type.text.SelectorTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.text.TextColorRegistryModule;
import org.lanternpowered.server.game.registry.type.text.TextSerializersRegistryModule;
import org.lanternpowered.server.game.registry.type.text.TextStyleRegistryModule;
import org.lanternpowered.server.game.registry.type.text.TranslationManagerRegistryModule;
import org.lanternpowered.server.game.registry.type.util.BanTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.util.RotationRegistryModule;
import org.lanternpowered.server.game.registry.type.world.DefaultGameRulesRegistryModule;
import org.lanternpowered.server.game.registry.type.world.DifficultyRegistryModule;
import org.lanternpowered.server.game.registry.type.world.DimensionTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.world.GeneratorModifierRegistryModule;
import org.lanternpowered.server.game.registry.type.world.GeneratorTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.world.PortalAgentTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.world.SerializationBehaviorRegistryModule;
import org.lanternpowered.server.game.registry.type.world.WeatherTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.world.WorldArchetypeRegistryModule;
import org.lanternpowered.server.game.registry.type.world.biome.BiomeRegistryModule;
import org.lanternpowered.server.game.registry.util.RegistryHelper;
import org.lanternpowered.server.resourcepack.LanternResourcePackFactory;
import org.lanternpowered.server.scheduler.LanternTaskBuilder;
import org.lanternpowered.server.scoreboard.LanternObjectiveBuilder;
import org.lanternpowered.server.scoreboard.LanternScoreboardBuilder;
import org.lanternpowered.server.scoreboard.LanternTeamBuilder;
import org.lanternpowered.server.status.LanternFavicon;
import org.lanternpowered.server.text.selector.LanternSelectorBuilder;
import org.lanternpowered.server.text.selector.LanternSelectorFactory;
import org.lanternpowered.server.text.translation.TranslationManager;
import org.lanternpowered.server.util.LanguageUtil;
import org.lanternpowered.server.util.graph.DirectedGraph;
import org.lanternpowered.server.util.graph.TopologicalOrder;
import org.lanternpowered.server.world.LanternWorldArchetypeBuilder;
import org.lanternpowered.server.world.extent.LanternExtentBufferFactory;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.boss.BossBarOverlay;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.data.persistence.DataFormat;
import org.spongepowered.api.data.type.Art;
import org.spongepowered.api.data.type.BigMushroomType;
import org.spongepowered.api.data.type.BigMushroomTypes;
import org.spongepowered.api.data.type.BrickType;
import org.spongepowered.api.data.type.BrickTypes;
import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.data.type.ComparatorType;
import org.spongepowered.api.data.type.ComparatorTypes;
import org.spongepowered.api.data.type.DirtType;
import org.spongepowered.api.data.type.DirtTypes;
import org.spongepowered.api.data.type.DisguisedBlockType;
import org.spongepowered.api.data.type.DisguisedBlockTypes;
import org.spongepowered.api.data.type.DoublePlantType;
import org.spongepowered.api.data.type.DoublePlantTypes;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.Hinge;
import org.spongepowered.api.data.type.Hinges;
import org.spongepowered.api.data.type.HorseColor;
import org.spongepowered.api.data.type.HorseStyle;
import org.spongepowered.api.data.type.InstrumentType;
import org.spongepowered.api.data.type.LogAxes;
import org.spongepowered.api.data.type.LogAxis;
import org.spongepowered.api.data.type.NotePitch;
import org.spongepowered.api.data.type.OcelotType;
import org.spongepowered.api.data.type.PistonType;
import org.spongepowered.api.data.type.PistonTypes;
import org.spongepowered.api.data.type.PlantType;
import org.spongepowered.api.data.type.PlantTypes;
import org.spongepowered.api.data.type.PortionType;
import org.spongepowered.api.data.type.PortionTypes;
import org.spongepowered.api.data.type.PrismarineType;
import org.spongepowered.api.data.type.PrismarineTypes;
import org.spongepowered.api.data.type.Profession;
import org.spongepowered.api.data.type.QuartzType;
import org.spongepowered.api.data.type.QuartzTypes;
import org.spongepowered.api.data.type.RabbitType;
import org.spongepowered.api.data.type.SandType;
import org.spongepowered.api.data.type.SandTypes;
import org.spongepowered.api.data.type.SandstoneType;
import org.spongepowered.api.data.type.SandstoneTypes;
import org.spongepowered.api.data.type.ShrubType;
import org.spongepowered.api.data.type.ShrubTypes;
import org.spongepowered.api.data.type.SkinPart;
import org.spongepowered.api.data.type.SlabType;
import org.spongepowered.api.data.type.SlabTypes;
import org.spongepowered.api.data.type.StoneType;
import org.spongepowered.api.data.type.StoneTypes;
import org.spongepowered.api.data.type.ToolType;
import org.spongepowered.api.data.type.TreeType;
import org.spongepowered.api.data.type.TreeTypes;
import org.spongepowered.api.data.type.WallType;
import org.spongepowered.api.data.type.WallTypes;
import org.spongepowered.api.data.value.ValueFactory;
import org.spongepowered.api.effect.particle.BlockParticle;
import org.spongepowered.api.effect.particle.ColoredParticle;
import org.spongepowered.api.effect.particle.ItemParticle;
import org.spongepowered.api.effect.particle.NoteParticle;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ResizableParticle;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.effect.sound.SoundCategory;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.ai.task.AITaskType;
import org.spongepowered.api.entity.ai.task.AbstractAITask;
import org.spongepowered.api.entity.living.Agent;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.source.BlockDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.FallingBlockDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.cause.entity.dismount.DismountType;
import org.spongepowered.api.event.cause.entity.spawn.BlockSpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.BreedingSpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.MobSpawnerSpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnType;
import org.spongepowered.api.event.cause.entity.spawn.WeatherSpawnCause;
import org.spongepowered.api.event.cause.entity.teleport.EntityTeleportCause;
import org.spongepowered.api.event.cause.entity.teleport.PortalTeleportCause;
import org.spongepowered.api.event.cause.entity.teleport.TeleportCause;
import org.spongepowered.api.event.cause.entity.teleport.TeleportType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.merchant.VillagerRegistry;
import org.spongepowered.api.item.recipe.RecipeRegistry;
import org.spongepowered.api.network.status.Favicon;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.AlternateCatalogRegistryModule;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.registry.FactoryRegistry;
import org.spongepowered.api.registry.RegistrationPhase;
import org.spongepowered.api.registry.RegistryModule;
import org.spongepowered.api.registry.util.CustomCatalogRegistration;
import org.spongepowered.api.registry.util.DelayedRegistration;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.resourcepack.ResourcePackFactory;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.scoreboard.CollisionRule;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.scoreboard.Visibility;
import org.spongepowered.api.scoreboard.critieria.Criterion;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlot;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayMode;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.statistic.BlockStatistic;
import org.spongepowered.api.statistic.EntityStatistic;
import org.spongepowered.api.statistic.ItemStatistic;
import org.spongepowered.api.statistic.Statistic;
import org.spongepowered.api.statistic.StatisticGroup;
import org.spongepowered.api.statistic.TeamStatistic;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.chat.ChatVisibility;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.selector.Selector;
import org.spongepowered.api.text.selector.SelectorType;
import org.spongepowered.api.text.serializer.TextSerializerFactory;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.ResettableBuilder;
import org.spongepowered.api.util.RespawnLocation;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanType;
import org.spongepowered.api.util.rotation.Rotation;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.PortalAgentType;
import org.spongepowered.api.world.SerializationBehavior;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.extent.ExtentBufferFactory;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.weather.Weather;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.Nullable;

@NonnullByDefault
public class LanternGameRegistry implements GameRegistry {

    private final LanternGame game;
    private final LanternResourcePackFactory resourcePackFactory = new LanternResourcePackFactory();
    private final LanternAttributeCalculator attributeCalculator = new LanternAttributeCalculator();

    private final Map<Class<? extends CatalogType>, CatalogRegistryModule<?>> catalogRegistryMap = Maps.newIdentityHashMap();
    private final Map<Class<? extends RegistryModule>, RegistryModule> classMap = Maps.newIdentityHashMap();
    private final Map<Class<?>, Supplier<?>> builderSupplierMap = Maps.newIdentityHashMap();
    private final List<Class<? extends RegistryModule>> orderedModules = new ArrayList<>();
    private final Set<RegistryModule> registryModules = new HashSet<>();

    // The phase of the registrations, this starts at null to define the early state.
    @Nullable private RegistrationPhase phase = null;
    // Whether all the modules are synced
    private boolean modulesSynced = true;

    public LanternGameRegistry(LanternGame game) {
        this.game = game;
    }

    public void registerDefaults() {
        this.registerModule(LanternOperation.class, new AttributeOperationRegistryModule())
                .registerModule(LanternAttribute.class, new AttributeRegistryModule())
                .registerModule(new AttributeTargetRegistryModule())
                .registerModule(BlockType.class, BlockRegistryModule.get())
                .registerModule(BlockState.class, new BlockStateRegistryModule())
                .registerModule(BossBarColor.class, new BossBarColorRegistryModule())
                .registerModule(BossBarOverlay.class, new BossBarOverlayRegistryModule())
                .registerModule(DamageType.class, new DamageTypeRegistryModule())
                .registerModule(DismountType.class, new DismountTypeRegistryModule())
                .registerModule(SpawnType.class, new SpawnTypeRegistryModule())
                .registerModule(TeleportType.class, new TeleportTypeRegistryModule())
                .registerModule(DataFormat.class, new DataFormatRegistryModule())
                .registerModule(BigMushroomType.class, new EnumValueRegistryModule<>(LanternBigMushroomType.class, BigMushroomTypes.class))
                .registerModule(BrickType.class, new EnumValueRegistryModule<>(LanternBrickType.class, BrickTypes.class))
                .registerModule(ComparatorType.class, new EnumValueRegistryModule<>(LanternComparatorType.class, ComparatorTypes.class))
                .registerModule(DirtType.class, new EnumValueRegistryModule<>(LanternDirtType.class, DirtTypes.class))
                .registerModule(DisguisedBlockType.class, new EnumValueRegistryModule<>(LanternDisguisedBlockType.class, DisguisedBlockTypes.class))
                .registerModule(LanternDoorHalf.class, new EnumValueRegistryModule<>(LanternDoorHalf.class, null))
                .registerModule(DoublePlantType.class, new EnumValueRegistryModule<>(LanternDoublePlantType.class, DoublePlantTypes.class))
                .registerModule(Art.class, new ArtRegistryModule())
                .registerModule(Career.class, new CareerRegistryModule())
                .registerModule(HandType.class, new HandTypeRegistryModule())
                .registerModule(HorseColor.class, new HorseColorRegistryModule())
                .registerModule(HorseStyle.class, new HorseStyleRegistryModule())
                .registerModule(InstrumentType.class, new InstrumentTypeRegistryModule())
                .registerModule(Hinge.class, new EnumValueRegistryModule<>(LanternHinge.class, Hinges.class))
                .registerModule(new KeyRegistryModule())
                .registerModule(LogAxis.class, new EnumValueRegistryModule<>(LanternLogAxis.class, LogAxes.class))
                .registerModule(NotePitch.class, new NotePitchRegistryModule())
                .registerModule(OcelotType.class, new OcelotTypeRegistryModule())
                .registerModule(Profession.class, new ProfessionRegistryModule())
                .registerModule(RabbitType.class, new RabbitTypeRegistryModule())
                .registerModule(ToolType.class, new ToolTypeRegistryModule())
                .registerModule(PistonType.class, new EnumValueRegistryModule<>(LanternPistonType.class, PistonTypes.class))
                .registerModule(PlantType.class, new EnumValueRegistryModule<>(LanternPlantType.class, PlantTypes.class))
                .registerModule(PortionType.class, new EnumValueRegistryModule<>(LanternPortionType.class, PortionTypes.class))
                .registerModule(PrismarineType.class, new EnumValueRegistryModule<>(LanternPrismarineType.class, PrismarineTypes.class))
                .registerModule(QuartzType.class, new EnumValueRegistryModule<>(LanternQuartzType.class, QuartzTypes.class))
                .registerModule(SandstoneType.class, new EnumValueRegistryModule<>(LanternSandstoneType.class, SandstoneTypes.class))
                .registerModule(SandType.class, new EnumValueRegistryModule<>(LanternSandType.class, SandTypes.class))
                .registerModule(ShrubType.class, new EnumValueRegistryModule<>(LanternShrubType.class, ShrubTypes.class))
                .registerModule(StoneType.class, new EnumValueRegistryModule<>(LanternStoneType.class, StoneTypes.class))
                .registerModule(SlabType.class, new EnumValueRegistryModule<>(LanternSlabType.class, SlabTypes.class))
                .registerModule(TreeType.class, new EnumValueRegistryModule<>(LanternTreeType.class, TreeTypes.class))
                .registerModule(WallType.class, new EnumValueRegistryModule<>(LanternWallType.class, WallTypes.class))
                .registerModule(SkinPart.class, new SkinPartRegistryModule())
                .registerModule(TransactionType.class, new TransactionTypeRegistryModule())
                .registerModule(ParticleType.class, new ParticleTypeRegistryModule())
                .registerModule(PotionEffectType.class, PotionEffectTypeRegistryModule.getInstance())
                .registerModule(SoundCategory.class, new SoundCategoryRegistryModule())
                .registerModule(SoundType.class, new SoundTypeRegistryModule())
                .registerModule(GameMode.class, GameModeRegistryModule.getInstance())
                .registerModule(EquipmentType.class, new EquipmentTypeRegistryModule())
                .registerModule(ItemType.class, ItemRegistryModule.get())
                .registerModule(CollisionRule.class, new CollisionRuleRegistryModule())
                .registerModule(Criterion.class, new CriterionRegistryModule())
                .registerModule(DisplaySlot.class, new DisplaySlotRegistryModule())
                .registerModule(ObjectiveDisplayMode.class, new ObjectiveDisplayModeRegistryModule())
                .registerModule(Visibility.class, new VisibilityRegistryModule())
                .registerModule(new ArgumentTypeRegistryModule())
                .registerModule(ChatType.class, new ChatTypeRegistryModule())
                .registerModule(ChatVisibility.class, new ChatVisibilityRegistryModule())
                .registerModule(new LocaleRegistryModule())
                .registerModule(new SelectorFactoryRegistryModule())
                .registerModule(SelectorType.class, new SelectorTypeRegistryModule())
                .registerModule(TextColor.class, new TextColorRegistryModule())
                .registerModule(new TextSerializersRegistryModule())
                .registerModule(TextStyle.Base.class, new TextStyleRegistryModule())
                .registerModule(new TranslationManagerRegistryModule())
                .registerModule(BanType.class, new BanTypeRegistryModule())
                .registerModule(Rotation.class, new RotationRegistryModule())
                .registerModule(BiomeType.class, BiomeRegistryModule.get())
                .registerModule(new DefaultGameRulesRegistryModule())
                .registerModule(Difficulty.class, DifficultyRegistryModule.getInstance())
                .registerModule(DimensionType.class, new DimensionTypeRegistryModule())
                .registerModule(WorldGeneratorModifier.class, new GeneratorModifierRegistryModule())
                .registerModule(GeneratorType.class, new GeneratorTypeRegistryModule())
                .registerModule(PortalAgentType.class, new PortalAgentTypeRegistryModule())
                .registerModule(SerializationBehavior.class, new SerializationBehaviorRegistryModule())
                .registerModule(Weather.class, new WeatherTypeRegistryModule())
                .registerModule(WorldArchetype.class, new WorldArchetypeRegistryModule())
                ;
        this.registerBuilderSupplier(LanternAttributeBuilder.class, LanternAttributeBuilder::new)
                .registerBuilderSupplier(BlockState.Builder.class, LanternBlockStateBuilder::new)
                .registerBuilderSupplier(BlockSnapshot.Builder.class, LanternBlockSnapshotBuilder::new)
                .registerBuilderSupplier(WorldArchetype.Builder.class, LanternWorldArchetypeBuilder::new)
                .registerBuilderSupplier(ParticleEffect.Builder.class, LanternParticleEffectBuilder::new)
                .registerBuilderSupplier(NoteParticle.Builder.class, LanternParticleEffectBuilder.Note::new)
                .registerBuilderSupplier(ResizableParticle.Builder.class, LanternParticleEffectBuilder.Resizable::new)
                .registerBuilderSupplier(ColoredParticle.Builder.class, LanternParticleEffectBuilder.Colorable::new)
                .registerBuilderSupplier(ItemParticle.Builder.class, LanternParticleEffectBuilder.Item::new)
                .registerBuilderSupplier(BlockParticle.Builder.class, LanternParticleEffectBuilder.Block::new)
                .registerBuilderSupplier(PotionEffect.Builder.class, LanternPotionEffectBuilder::new)
                .registerBuilderSupplier(Task.Builder.class, () -> new LanternTaskBuilder(Lantern.getGame().getScheduler()))
                .registerBuilderSupplier(Ban.Builder.class, BanBuilder::new)
                .registerBuilderSupplier(TabListEntry.Builder.class, LanternTabListEntryBuilder::new)
                .registerBuilderSupplier(Selector.Builder.class, LanternSelectorBuilder::new)
                .registerBuilderSupplier(Objective.Builder.class, LanternObjectiveBuilder::new)
                .registerBuilderSupplier(Scoreboard.Builder.class, LanternScoreboardBuilder::new)
                .registerBuilderSupplier(Team.Builder.class, LanternTeamBuilder::new)
                .registerBuilderSupplier(ServerBossBar.Builder.class, LanternBossBarBuilder::new)
                .registerBuilderSupplier(BlockSpawnCause.Builder.class, LanternBlockSpawnCauseBuilder::new)
                .registerBuilderSupplier(BreedingSpawnCause.Builder.class, LanternBreedingSpawnCauseBuilder::new)
                .registerBuilderSupplier(EntitySpawnCause.Builder.class, LanternEntitySpawnCauseBuilder::new)
                .registerBuilderSupplier(MobSpawnerSpawnCause.Builder.class, LanternMobSpawnerSpawnCauseBuilder::new)
                .registerBuilderSupplier(SpawnCause.Builder.class, LanternSpawnCauseBuilder::new)
                .registerBuilderSupplier(WeatherSpawnCause.Builder.class, LanternWeatherSpawnCauseBuilder::new)
                .registerBuilderSupplier(EntityTeleportCause.Builder.class, LanternEntityTeleportCauseBuilder::new)
                .registerBuilderSupplier(PortalTeleportCause.Builder.class, LanternPortalTeleportCauseBuilder::new)
                .registerBuilderSupplier(TeleportCause.Builder.class, LanternTeleportCauseBuilder::new)
                .registerBuilderSupplier(BlockDamageSource.Builder.class, LanternBlockDamageSourceBuilder::new)
                .registerBuilderSupplier(DamageSource.Builder.class, LanternDamageSourceBuilder::new)
                .registerBuilderSupplier(EntityDamageSource.Builder.class, LanternEntityDamageSourceBuilder::new)
                .registerBuilderSupplier(FallingBlockDamageSource.Builder.class, LanternFallingBlockDamageSourceBuilder::new)
                .registerBuilderSupplier(IndirectEntityDamageSource.Builder.class, LanternIndirectEntityDamageSourceBuilder::new)
                .registerBuilderSupplier(RespawnLocation.Builder.class, RespawnLocation.Builder::new)
                ;
        this.registerFactories();
    }

    private void registerFactories() {
        final List<FactoryRegistry<?, ?>> factoryRegistries = Lists.newArrayList();
        factoryRegistries.add(new ResourcePackFactoryModule());

        try {
            for (FactoryRegistry<?, ?> registry : factoryRegistries) {
                RegistryHelper.setFactory(registry.getFactoryOwner(), registry.provideFactory());
                registry.initialize();
            }
        } catch (Exception e) {
            this.game.getLogger().error("Could not initialize a factory!", e);
        }
    }

    @Override
    public <T> LanternGameRegistry registerBuilderSupplier(Class<T> builderClass, Supplier<? extends T> supplier) {
        checkArgument(!this.builderSupplierMap.containsKey(builderClass), "Already registered a builder supplier!");
        this.builderSupplierMap.put(builderClass, supplier);
        return this;
    }

    /**
     * Gets the {@link RegistryModule} for the specified class type.
     *
     * @param moduleType the module type
     * @param <T> the type of the registry module
     * @return the registry module if found, otherwise {@link Optional#empty()}
     */
    public <T extends RegistryModule> Optional<T> getRegistryModule(Class<T> moduleType) {
        return Optional.ofNullable((T) this.classMap.get(moduleType));
    }

    /**
     * Gets the {@link RegistryModule} for the specified class type.
     *
     * @param catalogType the catalog type
     * @param <T> the type of the registry module
     * @return the registry module if found, otherwise {@link Optional#empty()}
     */
    public <T extends CatalogType> Optional<CatalogRegistryModule<T>> getCatalogRegistryModule(Class<T> catalogType) {
        return Optional.ofNullable((CatalogRegistryModule) this.catalogRegistryMap.get(catalogType));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CatalogType> Optional<T> getType(Class<T> typeClass, String id) {
        CatalogRegistryModule<T> registryModule = this.getCatalogRegistryModule(typeClass).orElse(null);
        if (registryModule == null) {
            return Optional.empty();
        } else {
            return registryModule.getById(id.toLowerCase());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CatalogType> Collection<T> getAllOf(Class<T> typeClass) {
        CatalogRegistryModule<T> registryModule = this.getCatalogRegistryModule(typeClass).orElse(null);
        if (registryModule == null) {
            return Collections.emptyList();
        } else {
            return registryModule.getAll();
        }
    }

    @Override
    public <T extends CatalogType> Collection<T> getAllFor(String pluginId, Class<T> typeClass) {
        checkNotNull(pluginId);
        final CatalogRegistryModule<T> registryModule = this.getCatalogRegistryModule(typeClass).orElse(null);
        if (registryModule == null) {
            return Collections.emptyList();
        } else {
            ImmutableList.Builder<T> builder = ImmutableList.builder();
            registryModule.getAll().stream()
                    .filter(type -> pluginId.equals(type.getId().split(":")[0]))
                    .forEach(builder::add);
            return builder.build();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ResettableBuilder<?, ? super T>> T createBuilder(Class<T> builderClass) throws IllegalArgumentException {
        checkNotNull(builderClass, "Builder class was null!");
        checkArgument(this.builderSupplierMap.containsKey(builderClass), "Could not find a Supplier for the provided class: " +
                builderClass.getCanonicalName());
        return (T) this.builderSupplierMap.get(builderClass).get();
    }

    @Override
    public <T extends CatalogType> T register(Class<T> type, T obj) throws IllegalArgumentException, UnsupportedOperationException {
        CatalogRegistryModule<T> registryModule = this.getCatalogRegistryModule(type).orElse(null);
        if (registryModule == null) {
            throw new UnsupportedOperationException("Failed to find a RegistryModule for that type.");
        } else {
            if (registryModule instanceof AdditionalCatalogRegistryModule) {
                ((AdditionalCatalogRegistryModule<T>) registryModule).registerAdditionalCatalog(obj);
                return obj;
            }
            throw new UnsupportedOperationException("This catalog type does not support additional registration");
        }
    }

    @Override
    public <T extends CatalogType> LanternGameRegistry registerModule(Class<T> catalogClass, CatalogRegistryModule<T> registryModule)
            throws IllegalArgumentException, UnsupportedOperationException {
        checkArgument(!this.catalogRegistryMap.containsKey(catalogClass), "Already registered a registry module!");
        this.catalogRegistryMap.put(catalogClass, registryModule);
        if (this.phase != null && this.phase != RegistrationPhase.PRE_REGISTRY && catalogClass.getName().contains("org.spongepowered.api")) {
            throw new UnsupportedOperationException("Cannot register a module for an API defined class! That's the implementation's job!");
        }
        this.modulesSynced = false;
        return this;
    }

    @Override
    public LanternGameRegistry registerModule(RegistryModule module) {
        checkArgument(!this.registryModules.contains(module));
        this.registryModules.add(checkNotNull(module));
        this.modulesSynced = false;
        return this;
    }

    public void earlyRegistry() {
        this.registerModulePhase();
    }

    public void preRegistry() {
        this.phase = RegistrationPhase.PRE_REGISTRY;
        this.registerModulePhase();
    }

    public void preInit() {
        this.phase = RegistrationPhase.PRE_INIT;
        this.registerModulePhase();
    }

    public void init() {
        DataRegistrar.setupRegistrations(this.game);
        this.phase = RegistrationPhase.INIT;
        this.registerModulePhase();
    }

    public void postInit() {
        DataRegistrar.finalizeRegistrations(this.game);
        this.phase = RegistrationPhase.POST_INIT;
        this.registerModulePhase();
        this.phase = RegistrationPhase.LOADED;
    }

    private void syncModules() {
        if (this.modulesSynced) {
            return;
        }
        final DirectedGraph<Class<? extends RegistryModule>> graph = new DirectedGraph<>();
        for (RegistryModule aModule : this.registryModules) {
            if (!this.classMap.containsKey(aModule.getClass())) {
                this.classMap.put(aModule.getClass(), aModule);
            }
            this.addToGraph(aModule, graph);
        }
        // Now we need ot do the catalog ones
        for (CatalogRegistryModule<?> aModule : this.catalogRegistryMap.values()) {
            if (!this.classMap.containsKey(aModule.getClass())) {
                this.classMap.put(aModule.getClass(), aModule);
            }
            this.addToGraph(aModule, graph);
        }
        this.orderedModules.clear();
        this.orderedModules.addAll(TopologicalOrder.createOrderedLoad(graph));
        this.modulesSynced = true;
    }

    private void tryModulePhaseRegistration(RegistryModule module) {
        try {
            Set<Method> methods = this.getCustomRegistrations(module);
            methods.stream().filter(this::isProperPhase).forEach(method -> this.invokeCustomRegistration(module, method));
            if (this.isProperPhase(module)) {
                module.registerDefaults();
                for (CatalogMappingData data : this.getCatalogMappingData(module)) {
                    final Map<String, ?> mappings = data.getMappings();
                    if (mappings.isEmpty()) {
                        return;
                    }
                    RegistryHelper.mapFields(data.getTarget(), mappings, data.getIgnoredFields());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error trying to initialize module: " + module.getClass().getCanonicalName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<CatalogMappingData> getCatalogMappingData(RegistryModule module) {
        Map<String, ?> mappings = null;
        if (module instanceof AlternateCatalogRegistryModule) {
            mappings = checkNotNull(((AlternateCatalogRegistryModule) module).provideCatalogMap());
        }
        final List<CatalogMappingData> data = new ArrayList<>();
        for (Field field : module.getClass().getDeclaredFields()) {
            RegisterCatalog annotation = field.getAnnotation(RegisterCatalog.class);
            if (annotation != null) {
                if (mappings == null) {
                    try {
                        field.setAccessible(true);
                        mappings = (Map<String, ?>) field.get(module);
                        checkState(!mappings.isEmpty(), "The registered module: " + module.getClass().getSimpleName()
                                + " cannot have an empty mapping during registration!");
                    } catch (Exception e) {
                        this.game.getLogger().error("Failed to retrieve a registry field from module: " +
                                module.getClass().getCanonicalName(), e);
                    }
                }
                data.add(new CatalogMappingData(annotation, mappings));
            }
        }
        if (module instanceof CatalogMappingDataHolder) {
            data.addAll(((CatalogMappingDataHolder) module).getCatalogMappings());
        }
        return data;
    }

    private void invokeCustomRegistration(RegistryModule module, Method method) {
        try {
            method.invoke(module);
        } catch (IllegalAccessException | InvocationTargetException e) {
            this.game.getLogger().error("Error when calling custom catalog registration for module: "
                    + module.getClass().getCanonicalName(), e);
        }
    }

    private Set<Method> getCustomRegistrations(RegistryModule module) {
        ImmutableSet.Builder<Method> builder = ImmutableSet.builder();
        for (Method method : module.getClass().getMethods()) {
            CustomCatalogRegistration registration = method.getDeclaredAnnotation(CustomCatalogRegistration.class);
            if (registration != null) {
                builder.add(method);
            }
        }
        return builder.build();
    }

    /**
     * Gets whether the {@link RegistryModule} is applicable for the current phase.
     *
     * @param module the module
     * @return is proper phase
     */
    private boolean isProperPhase(RegistryModule module) {
        try {
            return this.isProperPhase(module.getClass().getMethod("registerDefaults"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isProperPhase(Method method) {
        if (method.getAnnotation(EarlyRegistration.class) != null) {
            return this.phase == null;
        }
        DelayedRegistration delay = method.getAnnotation(DelayedRegistration.class);
        if (delay == null) {
            return this.phase == RegistrationPhase.PRE_REGISTRY;
        } else {
            return this.phase == delay.value();
        }
    }

    private void registerModulePhase() {
        this.syncModules();
        for (Class<? extends RegistryModule> moduleClass : this.orderedModules) {
            if (!this.classMap.containsKey(moduleClass)) {
                throw new IllegalStateException("Something funky happened!");
            }
            this.tryModulePhaseRegistration(this.classMap.get(moduleClass));
        }
        this.registerAdditionalPhase();
    }

    private void registerAdditionalPhase() {
        for (Class<? extends RegistryModule> moduleClass : this.orderedModules) {
            final RegistryModule module = this.classMap.get(moduleClass);
            //RegistryModuleLoader.tryAdditionalRegistration(module);
        }
    }

    private void addToGraph(RegistryModule module, DirectedGraph<Class<? extends RegistryModule>> graph) {
        graph.add(module.getClass());
        RegistrationDependency dependency = module.getClass().getAnnotation(RegistrationDependency.class);
        if (dependency != null) {
            for (Class<? extends RegistryModule> dependent : dependency.value()) {
                graph.addEdge(checkNotNull(module.getClass(), "Dependency class was null!"), dependent);
            }
        }
    }

    /**
     * Gets the {@link GeneratorModifierRegistryModule}.
     *
     * @return the world generator modifier registry
     */
    public GeneratorModifierRegistryModule getWorldGeneratorModifierRegistry() {
        return this.getRegistryModule(GeneratorModifierRegistryModule.class).get();
    }

    /**
     * Gets the {@link AttributeRegistryModule}.
     *
     * @return the attribute registry
     */
    public AttributeRegistryModule getAttributeRegistry() {
        return this.getRegistryModule(AttributeRegistryModule.class).get();
    }

    /**
     * Gets the {@link BlockRegistryModule}.
     *
     * @return the block registry
     */
    public BlockRegistryModule getBlockRegistry() {
        return this.getRegistryModule(BlockRegistryModule.class).get();
    }

    /**
     * Gets the {@link ItemRegistryModule}.
     *
     * @return the item registry
     */
    public ItemRegistryModule getItemRegistry() {
        return this.getRegistryModule(ItemRegistryModule.class).get();
    }

    /**
     * Gets the {@link BiomeRegistryModule}.
     *
     * @return the biome registry
     */
    public BiomeRegistryModule getBiomeRegistry() {
        return this.getRegistryModule(BiomeRegistryModule.class).get();
    }

    /**
     * Gets the {@link ResourcePackFactory}.
     *
     * @return the resource pack factory
     */
    public LanternResourcePackFactory getResourcePackFactory() {
        return this.resourcePackFactory;
    }

    /**
     * Gets the {@link TranslationManager}.
     *
     * @return the translation manager
     */
    public TranslationManager getTranslationManager() {
        return this.getRegistryModule(TranslationManagerRegistryModule.class).get().getTranslationManager();
    }

    public LanternAttributeCalculator getAttributeCalculator() {
        return this.attributeCalculator;
    }

    @Override
    public Collection<String> getDefaultGameRules() {
        return this.getRegistryModule(DefaultGameRulesRegistryModule.class).get().get();
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
    public Optional<Rotation> getRotationFromDegree(int degrees) {
        return this.getRegistryModule(RotationRegistryModule.class).get().getRotationFromDegree(degrees);
    }

    @Override
    public Favicon loadFavicon(String raw) throws IOException {
        return LanternFavicon.load(raw);
    }

    @Override
    public Favicon loadFavicon(Path path) throws IOException {
        return LanternFavicon.load(path);
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
    public RecipeRegistry getRecipeRegistry() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ResourcePack> getResourcePackById(String id) {
        return this.resourcePackFactory.getById(id);
    }

    @Override
    public Optional<DisplaySlot> getDisplaySlotForColor(TextColor color) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Translation> getTranslationById(String id) {
        return this.getTranslationManager().getIfPresent(id);
    }

    @Override
    public ExtentBufferFactory getExtentBufferFactory() {
        return LanternExtentBufferFactory.INSTANCE;
    }

    @Override
    public AITaskType registerAITaskType(Object plugin, String id, String name, Class<? extends AbstractAITask<? extends Agent>> aiClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ValueFactory getValueFactory() {
        return LanternValueFactory.getInstance();
    }

    @Override
    public VillagerRegistry getVillagerRegistry() {
        return null;
    }

    @Deprecated
    @Override
    public TextSerializerFactory getTextSerializerFactory() {
        return this.getRegistryModule(TextSerializersRegistryModule.class).get().getTextSerializerFactory();
    }

    @Deprecated
    @Override
    public LanternSelectorFactory getSelectorFactory() {
        return this.getRegistryModule(SelectorFactoryRegistryModule.class).get().getFactory();
    }

    @Override
    public Locale getLocale(String locale) {
        return LanguageUtil.get(locale);
    }

}
