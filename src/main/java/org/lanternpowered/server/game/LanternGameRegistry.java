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
package org.lanternpowered.server.game;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.spongepowered.api.CatalogKey.MINECRAFT_NAMESPACE;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.lanternpowered.api.catalog.CatalogKeys;
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.api.item.enchantment.EnchantmentTypeBuilder;
import org.lanternpowered.api.script.context.Parameter;
import org.lanternpowered.api.script.function.action.ActionType;
import org.lanternpowered.api.script.function.condition.ConditionType;
import org.lanternpowered.api.script.function.value.DoubleValueProviderType;
import org.lanternpowered.api.script.function.value.FloatValueProviderType;
import org.lanternpowered.api.script.function.value.IntValueProviderType;
import org.lanternpowered.api.shard.ShardRegistry;
import org.lanternpowered.api.x.XGameRegistry;
import org.lanternpowered.api.x.text.XTextFactory;
import org.lanternpowered.server.advancement.LanternAdvancementBuilder;
import org.lanternpowered.server.advancement.LanternAdvancementTreeBuilder;
import org.lanternpowered.server.advancement.LanternDisplayInfoBuilder;
import org.lanternpowered.server.advancement.criteria.LanternCriterionBuilder;
import org.lanternpowered.server.advancement.criteria.LanternScoreCriterionBuilder;
import org.lanternpowered.server.advancement.criteria.trigger.LanternFilteredTriggerBuilder;
import org.lanternpowered.server.advancement.criteria.trigger.LanternTriggerBuilder;
import org.lanternpowered.server.attribute.LanternAttribute;
import org.lanternpowered.server.attribute.LanternAttributeBuilder;
import org.lanternpowered.server.attribute.LanternAttributeCalculator;
import org.lanternpowered.server.attribute.LanternOperation;
import org.lanternpowered.server.block.BlockSnapshotBuilder;
import org.lanternpowered.server.block.LanternBlockSnapshotBuilder;
import org.lanternpowered.server.block.LanternBlockStateBuilder;
import org.lanternpowered.server.block.LanternLocatableBlockBuilder;
import org.lanternpowered.server.block.tile.LanternTileEntityArchetypeBuilder;
import org.lanternpowered.server.boss.LanternBossBarBuilder;
import org.lanternpowered.server.catalog.LanternCatalogKey;
import org.lanternpowered.server.catalog.LanternCatalogKeyBuilder;
import org.lanternpowered.server.cause.entity.damage.source.LanternBlockDamageSourceBuilder;
import org.lanternpowered.server.cause.entity.damage.source.LanternDamageSourceBuilder;
import org.lanternpowered.server.cause.entity.damage.source.LanternEntityDamageSourceBuilder;
import org.lanternpowered.server.cause.entity.damage.source.LanternFallingBlockDamageSourceBuilder;
import org.lanternpowered.server.cause.entity.damage.source.LanternIndirectEntityDamageSourceBuilder;
import org.lanternpowered.server.cause.entity.healing.source.LanternEntityHealingSourceBuilder;
import org.lanternpowered.server.cause.entity.healing.source.LanternHealingSourceBuilder;
import org.lanternpowered.server.config.user.ban.BanBuilder;
import org.lanternpowered.server.data.DataRegistrar;
import org.lanternpowered.server.data.LanternDataRegistrationBuilder;
import org.lanternpowered.server.data.key.LanternKeyBuilder;
import org.lanternpowered.server.data.type.LanternBigMushroomType;
import org.lanternpowered.server.data.type.LanternBrickType;
import org.lanternpowered.server.data.type.LanternComparatorType;
import org.lanternpowered.server.data.type.LanternDisguisedBlockType;
import org.lanternpowered.server.data.type.LanternDoorHalf;
import org.lanternpowered.server.data.type.LanternDoublePlantType;
import org.lanternpowered.server.data.type.LanternHinge;
import org.lanternpowered.server.data.type.LanternLogAxis;
import org.lanternpowered.server.data.type.LanternPistonType;
import org.lanternpowered.server.data.type.LanternPortionType;
import org.lanternpowered.server.data.type.LanternPrismarineType;
import org.lanternpowered.server.data.type.LanternWallType;
import org.lanternpowered.server.data.value.LanternValueFactory;
import org.lanternpowered.server.effect.entity.EntityEffectCollection;
import org.lanternpowered.server.effect.entity.EntityEffectType;
import org.lanternpowered.server.effect.entity.LanternEntityEffectCollectionBuilder;
import org.lanternpowered.server.effect.firework.LanternFireworkEffectBuilder;
import org.lanternpowered.server.effect.particle.LanternParticleEffectBuilder;
import org.lanternpowered.server.effect.potion.LanternPotionEffectBuilder;
import org.lanternpowered.server.effect.potion.PotionType;
import org.lanternpowered.server.effect.sound.LanternSoundTypeBuilder;
import org.lanternpowered.server.entity.living.player.tab.LanternTabListEntryBuilder;
import org.lanternpowered.server.event.LanternEventContextKeyBuilder;
import org.lanternpowered.server.event.registry.LanternGameRegistryRegisterEvent;
import org.lanternpowered.server.extra.accessory.Accessory;
import org.lanternpowered.server.fluid.LanternFluidStackBuilder;
import org.lanternpowered.server.fluid.LanternFluidStackSnapshotBuilder;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.CatalogMappingData;
import org.lanternpowered.server.game.registry.CatalogMappingDataHolder;
import org.lanternpowered.server.game.registry.EarlyRegistration;
import org.lanternpowered.server.game.registry.EnumValueRegistryModule;
import org.lanternpowered.server.game.registry.factory.ResourcePackFactoryModule;
import org.lanternpowered.server.game.registry.factory.TimingsFactoryRegistryModule;
import org.lanternpowered.server.game.registry.type.advancement.AdvancementCriterionModule;
import org.lanternpowered.server.game.registry.type.advancement.AdvancementRegistryModule;
import org.lanternpowered.server.game.registry.type.advancement.AdvancementTreeLayoutModule;
import org.lanternpowered.server.game.registry.type.advancement.AdvancementTreeRegistryModule;
import org.lanternpowered.server.game.registry.type.advancement.AdvancementTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.advancement.TriggerRegistryModule;
import org.lanternpowered.server.game.registry.type.attribute.AttributeOperationRegistryModule;
import org.lanternpowered.server.game.registry.type.attribute.AttributeRegistryModule;
import org.lanternpowered.server.game.registry.type.attribute.AttributeTargetRegistryModule;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.game.registry.type.block.BlockSoundGroupRegistryModule;
import org.lanternpowered.server.game.registry.type.block.BlockStateRegistryModule;
import org.lanternpowered.server.game.registry.type.block.TileEntityTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.bossbar.BossBarColorRegistryModule;
import org.lanternpowered.server.game.registry.type.bossbar.BossBarOverlayRegistryModule;
import org.lanternpowered.server.game.registry.type.cause.ConstantDamageSourceRegistryModule;
import org.lanternpowered.server.game.registry.type.cause.ConstantHealingSourceRegistryModule;
import org.lanternpowered.server.game.registry.type.cause.DamageModifierTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.cause.DamageTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.cause.DismountTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.cause.EventContextKeysModule;
import org.lanternpowered.server.game.registry.type.cause.HealingTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.cause.SpawnTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.cause.TeleportTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.ArmorTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.ArtRegistryModule;
import org.lanternpowered.server.game.registry.type.data.BannerPatternShapeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.CareerRegistryModule;
import org.lanternpowered.server.game.registry.type.data.CoalTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.CookedFishRegistryModule;
import org.lanternpowered.server.data.DataRegistrationRegistryModule;
import org.lanternpowered.server.game.registry.type.data.DataTranslatorRegistryModule;
import org.lanternpowered.server.game.registry.type.data.DirtTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.DyeColorRegistryModule;
import org.lanternpowered.server.game.registry.type.data.FishRegistryModule;
import org.lanternpowered.server.game.registry.type.data.GoldenAppleRegistryModule;
import org.lanternpowered.server.game.registry.type.data.HandPreferenceRegistryModule;
import org.lanternpowered.server.game.registry.type.data.HandTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.HorseColorRegistryModule;
import org.lanternpowered.server.game.registry.type.data.HorseStyleRegistryModule;
import org.lanternpowered.server.game.registry.type.data.InstrumentTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.KeyRegistryModule;
import org.lanternpowered.server.game.registry.type.data.LlamaVariantRegistryModule;
import org.lanternpowered.server.game.registry.type.data.NotePitchRegistryModule;
import org.lanternpowered.server.game.registry.type.data.OcelotTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.PickupRuleRegistryModule;
import org.lanternpowered.server.game.registry.type.data.PlantTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.ProfessionRegistryModule;
import org.lanternpowered.server.game.registry.type.data.QuartzTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.RabbitTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.RailDirectionRegistryModule;
import org.lanternpowered.server.game.registry.type.data.RecordTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.SandTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.SandstoneTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.ShrubTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.SkinPartRegistryModule;
import org.lanternpowered.server.game.registry.type.data.SkullTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.SlabTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.StoneTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.ToolTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.TreeTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.persistence.DataFormatRegistryModule;
import org.lanternpowered.server.game.registry.type.economy.TransactionTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.effect.ParticleOptionRegistryModule;
import org.lanternpowered.server.game.registry.type.effect.ParticleTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.effect.PotionEffectTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.effect.PotionTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.effect.sound.SoundCategoryRegistryModule;
import org.lanternpowered.server.game.registry.type.effect.sound.SoundTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.effect.sound.entity.EntityEffectTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.entity.EntityTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.entity.player.GameModeRegistryModule;
import org.lanternpowered.server.game.registry.type.extra.AccessoryRegistryModule;
import org.lanternpowered.server.game.registry.type.fluid.FluidTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.item.EnchantmentTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.item.FireworkShapeRegistryModule;
import org.lanternpowered.server.game.registry.type.item.ItemRegistryModule;
import org.lanternpowered.server.game.registry.type.item.ItemStackComparatorRegistryModule;
import org.lanternpowered.server.game.registry.type.item.inventory.ClientContainerRegistryModule;
import org.lanternpowered.server.game.registry.type.item.inventory.InventoryArchetypeRegistryModule;
import org.lanternpowered.server.game.registry.type.item.inventory.InventoryTransformationRegistryModule;
import org.lanternpowered.server.game.registry.type.item.inventory.QueryOperationRegistryModule;
import org.lanternpowered.server.game.registry.type.item.inventory.equipment.EquipmentTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.scoreboard.CollisionRuleRegistryModule;
import org.lanternpowered.server.game.registry.type.scoreboard.CriterionRegistryModule;
import org.lanternpowered.server.game.registry.type.scoreboard.DisplaySlotRegistryModule;
import org.lanternpowered.server.game.registry.type.scoreboard.ObjectiveDisplayModeRegistryModule;
import org.lanternpowered.server.game.registry.type.scoreboard.VisibilityRegistryModule;
import org.lanternpowered.server.game.registry.type.statistic.StatisticRegistryModule;
import org.lanternpowered.server.game.registry.type.statistic.StatisticTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.text.ArgumentTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.text.ChatTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.text.ChatVisibilityRegistryModule;
import org.lanternpowered.server.game.registry.type.text.SelectorFactoryRegistryModule;
import org.lanternpowered.server.game.registry.type.text.SelectorTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.text.TextColorRegistryModule;
import org.lanternpowered.server.game.registry.type.text.TextSerializerRegistryModule;
import org.lanternpowered.server.game.registry.type.text.TextStyleRegistryModule;
import org.lanternpowered.server.game.registry.type.text.TranslationManagerRegistryModule;
import org.lanternpowered.server.game.registry.type.util.BanTypeRegistryModule;
import org.lanternpowered.server.game.registry.type.util.RotationRegistryModule;
import org.lanternpowered.server.game.registry.type.world.BlockChangeFlagRegistryModule;
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
import org.lanternpowered.server.inventory.LanternInventoryArchetypeBuilder;
import org.lanternpowered.server.inventory.LanternInventoryBuilder;
import org.lanternpowered.server.inventory.LanternItemStackBuilder;
import org.lanternpowered.server.inventory.property.LanternEquipmentSlotType;
import org.lanternpowered.server.inventory.property.LanternGuiIdProperty;
import org.lanternpowered.server.inventory.property.LanternIdentifiable;
import org.lanternpowered.server.inventory.property.LanternIntProperty;
import org.lanternpowered.server.inventory.property.LanternInventoryCapacity;
import org.lanternpowered.server.inventory.property.LanternInventoryDimension;
import org.lanternpowered.server.inventory.property.LanternInventoryTitle;
import org.lanternpowered.server.inventory.property.LanternSlotIndex;
import org.lanternpowered.server.inventory.property.LanternSlotPos;
import org.lanternpowered.server.inventory.property.LanternSlotSide;
import org.lanternpowered.server.inventory.property.LanternStringProperty;
import org.lanternpowered.server.inventory.query.LanternQueryTransformationBuilder;
import org.lanternpowered.server.inventory.transaction.LanternInventoryTransactionResult;
import org.lanternpowered.server.item.enchantment.LanternEnchantmentBuilder;
import org.lanternpowered.server.item.enchantment.LanternEnchantmentTypeBuilder;
import org.lanternpowered.server.item.recipe.IIngredient;
import org.lanternpowered.server.item.recipe.LanternIngredientBuilder;
import org.lanternpowered.server.item.recipe.crafting.ICraftingRecipeRegistry;
import org.lanternpowered.server.item.recipe.crafting.IShapedCraftingRecipe;
import org.lanternpowered.server.item.recipe.crafting.IShapelessCraftingRecipe;
import org.lanternpowered.server.item.recipe.crafting.LanternCraftingRecipeRegistry;
import org.lanternpowered.server.item.recipe.crafting.LanternShapedCraftingRecipeBuilder;
import org.lanternpowered.server.item.recipe.crafting.LanternShapelessCraftingRecipeBuilder;
import org.lanternpowered.server.item.recipe.fuel.IFuel;
import org.lanternpowered.server.item.recipe.fuel.IFuelRegistry;
import org.lanternpowered.server.item.recipe.fuel.LanternFuelBuilder;
import org.lanternpowered.server.item.recipe.fuel.LanternFuelRegistryModule;
import org.lanternpowered.server.item.recipe.smelting.ISmeltingRecipe;
import org.lanternpowered.server.item.recipe.smelting.LanternSmeltingRecipeBuilder;
import org.lanternpowered.server.item.recipe.smelting.LanternSmeltingRecipeRegistry;
import org.lanternpowered.server.network.entity.EntityProtocolType;
import org.lanternpowered.server.network.entity.EntityProtocolTypeRegistryModule;
import org.lanternpowered.server.network.status.LanternFavicon;
import org.lanternpowered.server.network.tile.TileEntityProtocolType;
import org.lanternpowered.server.network.tile.TileEntityProtocolTypeRegistryModule;
import org.lanternpowered.server.plugin.InternalPluginsInfo;
import org.lanternpowered.server.resourcepack.LanternResourcePackFactory;
import org.lanternpowered.server.scheduler.LanternTaskBuilder;
import org.lanternpowered.server.scoreboard.LanternObjectiveBuilder;
import org.lanternpowered.server.scoreboard.LanternScoreboardBuilder;
import org.lanternpowered.server.scoreboard.LanternTeamBuilder;
import org.lanternpowered.server.script.context.ContextParameterRegistryModule;
import org.lanternpowered.server.script.function.action.ActionTypeRegistryModule;
import org.lanternpowered.server.script.function.condition.ConditionTypeRegistryModule;
import org.lanternpowered.server.script.function.value.DoubleValueProviderTypeRegistryModule;
import org.lanternpowered.server.script.function.value.FloatValueProviderTypeRegistryModule;
import org.lanternpowered.server.script.function.value.IntValueProviderTypeRegistryModule;
import org.lanternpowered.server.shard.LanternShardRegistry;
import org.lanternpowered.server.statistic.builder.BlockStatisticBuilder;
import org.lanternpowered.server.statistic.builder.EntityStatisticBuilder;
import org.lanternpowered.server.statistic.builder.ItemStatisticBuilder;
import org.lanternpowered.server.statistic.builder.StatisticBuilder;
import org.lanternpowered.server.text.LanternLiteralText;
import org.lanternpowered.server.text.LanternScoreText;
import org.lanternpowered.server.text.LanternSelectorText;
import org.lanternpowered.server.text.LanternTextFactory;
import org.lanternpowered.server.text.LanternTextTemplate;
import org.lanternpowered.server.text.LanternTranslatableText;
import org.lanternpowered.server.text.action.ChangePageClickActionBuilder;
import org.lanternpowered.server.text.action.ExecuteCallbackClickActionBuilder;
import org.lanternpowered.server.text.action.InsertTextShiftClickActionBuilder;
import org.lanternpowered.server.text.action.OpenUrlClickActionBuilder;
import org.lanternpowered.server.text.action.RunCommandClickActionBuilder;
import org.lanternpowered.server.text.action.ShowEntityHoverActionBuilder;
import org.lanternpowered.server.text.action.ShowEntityRefBuilder;
import org.lanternpowered.server.text.action.ShowItemHoverActionBuilder;
import org.lanternpowered.server.text.action.ShowTextHoverActionBuilder;
import org.lanternpowered.server.text.action.SuggestCommandClickActionBuilder;
import org.lanternpowered.server.text.selector.LanternSelectorBuilder;
import org.lanternpowered.server.text.selector.LanternSelectorFactory;
import org.lanternpowered.server.text.translation.TranslationManager;
import org.lanternpowered.server.util.LocaleCache;
import org.lanternpowered.server.util.graph.CyclicGraphException;
import org.lanternpowered.server.util.graph.DirectedGraph;
import org.lanternpowered.server.util.graph.TopologicalOrder;
import org.lanternpowered.server.world.LanternWorldArchetypeBuilder;
import org.lanternpowered.server.world.LanternWorldBorderBuilder;
import org.lanternpowered.server.world.biome.LanternBiomeGenerationSettingsBuilder;
import org.lanternpowered.server.world.biome.LanternVirtualBiomeTypeBuilder;
import org.lanternpowered.server.world.extent.LanternExtentBufferFactory;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementTree;
import org.spongepowered.api.advancement.AdvancementType;
import org.spongepowered.api.advancement.DisplayInfo;
import org.spongepowered.api.advancement.criteria.AdvancementCriterion;
import org.spongepowered.api.advancement.criteria.ScoreAdvancementCriterion;
import org.spongepowered.api.advancement.criteria.trigger.FilteredTrigger;
import org.spongepowered.api.advancement.criteria.trigger.Trigger;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.tileentity.TileEntityArchetype;
import org.spongepowered.api.block.tileentity.TileEntityType;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.boss.BossBarOverlay;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.persistence.DataFormat;
import org.spongepowered.api.data.persistence.DataTranslator;
import org.spongepowered.api.data.type.ArmorType;
import org.spongepowered.api.data.type.Art;
import org.spongepowered.api.data.type.BannerPatternShape;
import org.spongepowered.api.data.type.BigMushroomType;
import org.spongepowered.api.data.type.BigMushroomTypes;
import org.spongepowered.api.data.type.BrickType;
import org.spongepowered.api.data.type.BrickTypes;
import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.data.type.CoalType;
import org.spongepowered.api.data.type.ComparatorType;
import org.spongepowered.api.data.type.ComparatorTypes;
import org.spongepowered.api.data.type.CookedFish;
import org.spongepowered.api.data.type.DirtType;
import org.spongepowered.api.data.type.DisguisedBlockType;
import org.spongepowered.api.data.type.DisguisedBlockTypes;
import org.spongepowered.api.data.type.DoublePlantType;
import org.spongepowered.api.data.type.DoublePlantTypes;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.Fish;
import org.spongepowered.api.data.type.GoldenApple;
import org.spongepowered.api.data.type.HandPreference;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.Hinge;
import org.spongepowered.api.data.type.Hinges;
import org.spongepowered.api.data.type.HorseColor;
import org.spongepowered.api.data.type.HorseStyle;
import org.spongepowered.api.data.type.InstrumentType;
import org.spongepowered.api.data.type.LlamaVariant;
import org.spongepowered.api.data.type.LogAxes;
import org.spongepowered.api.data.type.LogAxis;
import org.spongepowered.api.data.type.NotePitch;
import org.spongepowered.api.data.type.OcelotType;
import org.spongepowered.api.data.type.PickupRule;
import org.spongepowered.api.data.type.PistonType;
import org.spongepowered.api.data.type.PistonTypes;
import org.spongepowered.api.data.type.PlantType;
import org.spongepowered.api.data.type.PortionType;
import org.spongepowered.api.data.type.PortionTypes;
import org.spongepowered.api.data.type.PrismarineType;
import org.spongepowered.api.data.type.PrismarineTypes;
import org.spongepowered.api.data.type.Profession;
import org.spongepowered.api.data.type.QuartzType;
import org.spongepowered.api.data.type.RabbitType;
import org.spongepowered.api.data.type.RailDirection;
import org.spongepowered.api.data.type.SandType;
import org.spongepowered.api.data.type.SandstoneType;
import org.spongepowered.api.data.type.ShrubType;
import org.spongepowered.api.data.type.SkinPart;
import org.spongepowered.api.data.type.SkullType;
import org.spongepowered.api.data.type.SlabType;
import org.spongepowered.api.data.type.StoneType;
import org.spongepowered.api.data.type.ToolType;
import org.spongepowered.api.data.type.TreeType;
import org.spongepowered.api.data.type.WallType;
import org.spongepowered.api.data.type.WallTypes;
import org.spongepowered.api.data.value.ValueFactory;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOption;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.effect.sound.SoundCategory;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.record.RecordType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.ai.task.AITaskType;
import org.spongepowered.api.entity.ai.task.AbstractAITask;
import org.spongepowered.api.entity.living.Agent;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.event.cause.EventContextKey;
import org.spongepowered.api.event.cause.entity.damage.DamageModifierType;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.source.BlockDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.FallingBlockDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.cause.entity.dismount.DismountType;
import org.spongepowered.api.event.cause.entity.health.HealingType;
import org.spongepowered.api.event.cause.entity.health.source.EntityHealingSource;
import org.spongepowered.api.event.cause.entity.health.source.HealingSource;
import org.spongepowered.api.event.cause.entity.spawn.SpawnType;
import org.spongepowered.api.event.cause.entity.teleport.TeleportType;
import org.spongepowered.api.extra.fluid.FluidStack;
import org.spongepowered.api.extra.fluid.FluidStackSnapshot;
import org.spongepowered.api.extra.fluid.FluidType;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.FireworkShape;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryTransformation;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;
import org.spongepowered.api.item.inventory.property.GuiId;
import org.spongepowered.api.item.inventory.property.GuiIdProperty;
import org.spongepowered.api.item.inventory.property.Identifiable;
import org.spongepowered.api.item.inventory.property.IntProperty;
import org.spongepowered.api.item.inventory.property.InventoryCapacity;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.property.SlotSide;
import org.spongepowered.api.item.inventory.property.StringProperty;
import org.spongepowered.api.item.inventory.query.QueryOperationType;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.merchant.VillagerRegistry;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.item.recipe.crafting.ShapedCraftingRecipe;
import org.spongepowered.api.item.recipe.crafting.ShapelessCraftingRecipe;
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipe;
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipeRegistry;
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
import org.spongepowered.api.statistic.StatisticType;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.ScoreText;
import org.spongepowered.api.text.SelectorText;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.TranslatableText;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.ShiftClickAction;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.chat.ChatVisibility;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.selector.Selector;
import org.spongepowered.api.text.selector.SelectorType;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.ResettableBuilder;
import org.spongepowered.api.util.RespawnLocation;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanType;
import org.spongepowered.api.util.rotation.Rotation;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.PortalAgentType;
import org.spongepowered.api.world.SerializationBehavior;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.WorldBorder;
import org.spongepowered.api.world.biome.BiomeGenerationSettings;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.VirtualBiomeType;
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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.Nullable;

@Singleton
public class LanternGameRegistry implements XGameRegistry {

    private final LanternGame game;
    private final LanternResourcePackFactory resourcePackFactory = new LanternResourcePackFactory();
    private final LanternAttributeCalculator attributeCalculator = new LanternAttributeCalculator();

    private final LanternSmeltingRecipeRegistry smeltingRecipeRegistry = new LanternSmeltingRecipeRegistry();
    private final LanternCraftingRecipeRegistry craftingRecipeRegistry = new LanternCraftingRecipeRegistry();
    private final LanternFuelRegistryModule fuelRegistryModule = new LanternFuelRegistryModule();

    private final Map<Class<? extends CatalogType>, CatalogRegistryModule<?>> catalogRegistryMap = new IdentityHashMap<>();
    private final Map<Class<? extends RegistryModule>, RegistryModule> classMap = new IdentityHashMap<>();
    private final Map<Class<?>, Supplier<?>> builderSupplierMap = new IdentityHashMap<>();
    private final List<Class<? extends RegistryModule>> orderedModules = new ArrayList<>();
    private final Set<RegistryModule> registryModules = new HashSet<>();

    // The phase of the registrations, this starts at null to define the early state.
    @Nullable private RegistrationPhase phase = null;
    // Whether all the modules are synced
    private boolean modulesSynced = true;

    @Inject
    private LanternGameRegistry(LanternGame game) {
        this.game = game;
    }

    public void registerDefaults() {
        registerBuilderSupplier(LanternAttributeBuilder.class, LanternAttributeBuilder::new)
                .registerBuilderSupplier(BlockSnapshot.Builder.class, LanternBlockSnapshotBuilder::new)
                .registerBuilderSupplier(BlockSnapshotBuilder.class, LanternBlockSnapshotBuilder::new)
                .registerBuilderSupplier(BlockState.Builder.class, LanternBlockStateBuilder::new)
                .registerBuilderSupplier(TileEntityArchetype.Builder.class, LanternTileEntityArchetypeBuilder::new)
                .registerBuilderSupplier(LocatableBlock.Builder.class, LanternLocatableBlockBuilder::new)
                .registerBuilderSupplier(WorldArchetype.Builder.class, LanternWorldArchetypeBuilder::new)
                .registerBuilderSupplier(ParticleEffect.Builder.class, LanternParticleEffectBuilder::new)
                .registerBuilderSupplier(PotionEffect.Builder.class, LanternPotionEffectBuilder::new)
                .registerBuilderSupplier(Task.Builder.class, () -> new LanternTaskBuilder(Lantern.getGame().getScheduler()))
                .registerBuilderSupplier(Ban.Builder.class, BanBuilder::new)
                .registerBuilderSupplier(TabListEntry.Builder.class, LanternTabListEntryBuilder::new)
                .registerBuilderSupplier(Selector.Builder.class, LanternSelectorBuilder::new)
                .registerBuilderSupplier(Objective.Builder.class, LanternObjectiveBuilder::new)
                .registerBuilderSupplier(Scoreboard.Builder.class, LanternScoreboardBuilder::new)
                .registerBuilderSupplier(Team.Builder.class, LanternTeamBuilder::new)
                .registerBuilderSupplier(ServerBossBar.Builder.class, LanternBossBarBuilder::new)
                .registerBuilderSupplier(BlockDamageSource.Builder.class, LanternBlockDamageSourceBuilder::new)
                .registerBuilderSupplier(DamageSource.Builder.class, LanternDamageSourceBuilder::new)
                .registerBuilderSupplier(EntityDamageSource.Builder.class, LanternEntityDamageSourceBuilder::new)
                .registerBuilderSupplier(FallingBlockDamageSource.Builder.class, LanternFallingBlockDamageSourceBuilder::new)
                .registerBuilderSupplier(IndirectEntityDamageSource.Builder.class, LanternIndirectEntityDamageSourceBuilder::new)
                .registerBuilderSupplier(HealingSource.Builder.class, LanternHealingSourceBuilder::new)
                .registerBuilderSupplier(EntityHealingSource.Builder.class, LanternEntityHealingSourceBuilder::new)
                .registerBuilderSupplier(RespawnLocation.Builder.class, RespawnLocation.Builder::new)
                .registerBuilderSupplier(SoundType.Builder.class, LanternSoundTypeBuilder::new)
                .registerBuilderSupplier(EntityEffectCollection.Builder.class, LanternEntityEffectCollectionBuilder::new)
                .registerBuilderSupplier(FireworkEffect.Builder.class, LanternFireworkEffectBuilder::new)
                .registerBuilderSupplier(InventoryArchetype.Builder.class, LanternInventoryArchetypeBuilder::new)
                .registerBuilderSupplier(Inventory.Builder.class, LanternInventoryBuilder::create)
                .registerBuilderSupplier(InventoryTransformation.Builder.class, LanternQueryTransformationBuilder::new)
                .registerBuilderSupplier(BiomeGenerationSettings.Builder.class, LanternBiomeGenerationSettingsBuilder::new)
                .registerBuilderSupplier(VirtualBiomeType.Builder.class, LanternVirtualBiomeTypeBuilder::new)
                .registerBuilderSupplier(BlockStatisticBuilder.class, BlockStatisticBuilder::create)
                .registerBuilderSupplier(EntityStatisticBuilder.class, EntityStatisticBuilder::create)
                .registerBuilderSupplier(ItemStatisticBuilder.class, ItemStatisticBuilder::create)
                .registerBuilderSupplier(StatisticBuilder.class, StatisticBuilder::create)
                .registerBuilderSupplier(DataRegistration.Builder.class, LanternDataRegistrationBuilder::new)
                .registerBuilderSupplier(WorldBorder.Builder.class, LanternWorldBorderBuilder::new)
                .registerBuilderSupplier(FluidStack.Builder.class, LanternFluidStackBuilder::new)
                .registerBuilderSupplier(FluidStackSnapshot.Builder.class, LanternFluidStackSnapshotBuilder::new)
                .registerBuilderSupplier(ItemStack.Builder.class, LanternItemStackBuilder::new)
                .registerBuilderSupplier(EventContextKey.Builder.class, LanternEventContextKeyBuilder::new)
                .registerBuilderSupplier(Enchantment.Builder.class, LanternEnchantmentBuilder::new)
                .registerBuilderSupplier(Key.Builder.class, LanternKeyBuilder::new)
                .registerBuilderSupplier(InventoryTransactionResult.Builder.class, LanternInventoryTransactionResult.Builder::new)
                .registerBuilderSupplier(EnchantmentTypeBuilder.class, LanternEnchantmentTypeBuilder::new)
                .registerBuilderSupplier(CatalogKey.Builder.class, LanternCatalogKeyBuilder::new)
                // Text
                .registerBuilderSupplier(ScoreText.Builder.class, LanternScoreText.Builder::new)
                .registerBuilderSupplier(LiteralText.Builder.class, LanternLiteralText.Builder::new)
                .registerBuilderSupplier(SelectorText.Builder.class, LanternSelectorText.Builder::new)
                .registerBuilderSupplier(TranslatableText.Builder.class, LanternTranslatableText.Builder::new)
                .registerBuilderSupplier(ClickAction.ChangePage.Builder.class, ChangePageClickActionBuilder::new)
                .registerBuilderSupplier(ClickAction.ExecuteCallback.Builder.class, ExecuteCallbackClickActionBuilder::new)
                .registerBuilderSupplier(ClickAction.OpenUrl.Builder.class, OpenUrlClickActionBuilder::new)
                .registerBuilderSupplier(ClickAction.RunCommand.Builder.class, RunCommandClickActionBuilder::new)
                .registerBuilderSupplier(ClickAction.SuggestCommand.Builder.class, SuggestCommandClickActionBuilder::new)
                .registerBuilderSupplier(HoverAction.ShowEntity.Builder.class, ShowEntityHoverActionBuilder::new)
                .registerBuilderSupplier(HoverAction.ShowEntity.Ref.Builder.class, ShowEntityRefBuilder::new)
                .registerBuilderSupplier(HoverAction.ShowItem.Builder.class, ShowItemHoverActionBuilder::new)
                .registerBuilderSupplier(HoverAction.ShowText.Builder.class, ShowTextHoverActionBuilder::new)
                .registerBuilderSupplier(ShiftClickAction.InsertText.Builder.class, InsertTextShiftClickActionBuilder::new)
                .registerBuilderSupplier(TextTemplate.Arg.Builder.class, LanternTextTemplate.Arg.Builder::new)
                // Inventory properties
                .registerBuilderSupplier(SlotPos.Builder.class, LanternSlotPos.Builder::new)
                .registerBuilderSupplier(SlotSide.Builder.class, LanternSlotSide.Builder::new)
                .registerBuilderSupplier(StringProperty.Builder.class, LanternStringProperty.Builder::new)
                .registerBuilderSupplier(IntProperty.Builder.class, LanternIntProperty.Builder::new)
                .registerBuilderSupplier(SlotIndex.Builder.class, LanternSlotIndex.Builder::new)
                .registerBuilderSupplier(InventoryCapacity.Builder.class, LanternInventoryCapacity.Builder::new)
                .registerBuilderSupplier(Identifiable.Builder.class, LanternIdentifiable.Builder::new)
                .registerBuilderSupplier(GuiIdProperty.Builder.class, LanternGuiIdProperty.Builder::new)
                .registerBuilderSupplier(EquipmentSlotType.Builder.class, LanternEquipmentSlotType.Builder::new)
                .registerBuilderSupplier(InventoryDimension.Builder.class, LanternInventoryDimension.Builder::new)
                .registerBuilderSupplier(InventoryTitle.Builder.class, LanternInventoryTitle.Builder::new)
                // Advancements
                .registerBuilderSupplier(Advancement.Builder.class, LanternAdvancementBuilder::new)
                .registerBuilderSupplier(AdvancementTree.Builder.class, LanternAdvancementTreeBuilder::new)
                .registerBuilderSupplier(AdvancementCriterion.Builder.class, LanternCriterionBuilder::new)
                .registerBuilderSupplier(ScoreAdvancementCriterion.Builder.class, LanternScoreCriterionBuilder::new)
                .registerBuilderSupplier(Trigger.Builder.class, LanternTriggerBuilder::new)
                .registerBuilderSupplier(FilteredTrigger.Builder.class, LanternFilteredTriggerBuilder::new)
                .registerBuilderSupplier(DisplayInfo.Builder.class, LanternDisplayInfoBuilder::new)
                // Recipes
                .registerBuilderSupplier(ShapedCraftingRecipe.Builder.class, LanternShapedCraftingRecipeBuilder::new)
                .registerBuilderSupplier(IShapedCraftingRecipe.Builder.class, LanternShapedCraftingRecipeBuilder::new)
                .registerBuilderSupplier(ShapelessCraftingRecipe.Builder.class, LanternShapelessCraftingRecipeBuilder::new)
                .registerBuilderSupplier(IShapelessCraftingRecipe.Builder.class, LanternShapelessCraftingRecipeBuilder::new)
                .registerBuilderSupplier(SmeltingRecipe.Builder.class, LanternSmeltingRecipeBuilder::new)
                .registerBuilderSupplier(ISmeltingRecipe.Builder.class, LanternSmeltingRecipeBuilder::new)
                .registerBuilderSupplier(Ingredient.Builder.class, LanternIngredientBuilder::new)
                .registerBuilderSupplier(IIngredient.Builder.class, LanternIngredientBuilder::new)
                .registerBuilderSupplier(IFuel.Builder.class, LanternFuelBuilder::new)
        ;
        // All enum value enumerations must extend registry class, because very strange things
        // are happening. Without this, all the dummy fields are never updated???
        registerModule(LanternOperation.class, new AttributeOperationRegistryModule())
                .registerModule(LanternAttribute.class, new AttributeRegistryModule())
                .registerModule(new AttributeTargetRegistryModule())
                .registerModule(new BlockSoundGroupRegistryModule())
                .registerModule(BlockType.class, BlockRegistryModule.get())
                .registerModule(BlockState.class, new BlockStateRegistryModule())
                .registerModule(BossBarColor.class, new BossBarColorRegistryModule())
                .registerModule(BossBarOverlay.class, new BossBarOverlayRegistryModule())
                .registerModule(Accessory.class, new AccessoryRegistryModule())
                .registerModule(DamageType.class, new DamageTypeRegistryModule())
                .registerModule(DamageModifierType.class, new DamageModifierTypeRegistryModule())
                .registerModule(new ConstantDamageSourceRegistryModule())
                .registerModule(HealingType.class, new HealingTypeRegistryModule())
                .registerModule(new ConstantHealingSourceRegistryModule())
                .registerModule(DismountType.class, new DismountTypeRegistryModule())
                .registerModule(SpawnType.class, new SpawnTypeRegistryModule())
                .registerModule(TeleportType.class, new TeleportTypeRegistryModule())
                .registerModule(DataFormat.class, new DataFormatRegistryModule())
                .registerModule(BigMushroomType.class,
                        new EnumValueRegistryModule<BigMushroomType>(LanternBigMushroomType.class, BigMushroomTypes.class) {})
                .registerModule(BrickType.class,
                        new EnumValueRegistryModule<BrickType>(LanternBrickType.class, BrickTypes.class) {})
                .registerModule(ComparatorType.class,
                        new EnumValueRegistryModule<ComparatorType>(LanternComparatorType.class, ComparatorTypes.class) {})
                .registerModule(DirtType.class, DirtTypeRegistryModule.get())
                .registerModule(DisguisedBlockType.class,
                        new EnumValueRegistryModule<DisguisedBlockType>(LanternDisguisedBlockType.class, DisguisedBlockTypes.class) {})
                .registerModule(LanternDoorHalf.class,
                        new EnumValueRegistryModule<LanternDoorHalf>(LanternDoorHalf.class, null) {})
                .registerModule(DoublePlantType.class,
                        new EnumValueRegistryModule<DoublePlantType>(LanternDoublePlantType.class, DoublePlantTypes.class) {})
                .registerModule(Art.class, new ArtRegistryModule())
                .registerModule(Career.class, new CareerRegistryModule())
                .registerModule(HandType.class, new HandTypeRegistryModule())
                .registerModule(HandPreference.class, new HandPreferenceRegistryModule())
                .registerModule(HorseColor.class, new HorseColorRegistryModule())
                .registerModule(HorseStyle.class, new HorseStyleRegistryModule())
                .registerModule(InstrumentType.class, new InstrumentTypeRegistryModule())
                .registerModule(Hinge.class,
                        new EnumValueRegistryModule<Hinge>(LanternHinge.class, Hinges.class) {})
                .registerModule(Key.class, KeyRegistryModule.get())
                .registerModule(PortionType.class, new EnumValueRegistryModule<PortionType>(LanternPortionType.class, PortionTypes.class) {})
                .registerModule(LogAxis.class,
                        new EnumValueRegistryModule<LogAxis>(LanternLogAxis.class, LogAxes.class) {})
                .registerModule(NotePitch.class, NotePitchRegistryModule.get())
                .registerModule(OcelotType.class, new OcelotTypeRegistryModule())
                .registerModule(LlamaVariant.class, new LlamaVariantRegistryModule())
                .registerModule(Profession.class, new ProfessionRegistryModule())
                .registerModule(RabbitType.class, new RabbitTypeRegistryModule())
                .registerModule(ToolType.class, new ToolTypeRegistryModule())
                .registerModule(ArmorType.class, new ArmorTypeRegistryModule())
                .registerModule(PistonType.class,
                        new EnumValueRegistryModule<PistonType>(LanternPistonType.class, PistonTypes.class) {})
                .registerModule(PlantType.class, PlantTypeRegistryModule.get())
                .registerModule(PrismarineType.class,
                        new EnumValueRegistryModule<PrismarineType>(LanternPrismarineType.class, PrismarineTypes.class) {})
                .registerModule(QuartzType.class, QuartzTypeRegistryModule.get())
                .registerModule(SandstoneType.class, SandstoneTypeRegistryModule.get())
                .registerModule(SandType.class, SandTypeRegistryModule.get())
                .registerModule(ShrubType.class, ShrubTypeRegistryModule.get())
                .registerModule(StoneType.class, StoneTypeRegistryModule.get())
                .registerModule(SlabType.class, SlabTypeRegistryModule.get())
                .registerModule(TreeType.class, TreeTypeRegistryModule.get())
                .registerModule(WallType.class,
                        new EnumValueRegistryModule<WallType>(LanternWallType.class, WallTypes.class) {})
                .registerModule(SkinPart.class, new SkinPartRegistryModule())
                .registerModule(TransactionType.class, new TransactionTypeRegistryModule())
                .registerModule(ParticleType.class, new ParticleTypeRegistryModule())
                .registerModule(ParticleOption.class, new ParticleOptionRegistryModule())
                .registerModule(FireworkShape.class, FireworkShapeRegistryModule.get())
                .registerModule(PotionEffectType.class, PotionEffectTypeRegistryModule.get())
                .registerModule(SoundCategory.class, new SoundCategoryRegistryModule())
                .registerModule(SoundType.class, new SoundTypeRegistryModule())
                .registerModule(EntityEffectType.class, new EntityEffectTypeRegistryModule())
                .registerModule(GameMode.class, GameModeRegistryModule.get())
                .registerModule(EquipmentType.class, new EquipmentTypeRegistryModule())
                .registerModule(ItemType.class, ItemRegistryModule.get())
                .registerModule(CollisionRule.class, new CollisionRuleRegistryModule())
                .registerModule(Criterion.class, new CriterionRegistryModule())
                .registerModule(DisplaySlot.class, new DisplaySlotRegistryModule())
                .registerModule(ObjectiveDisplayMode.class, new ObjectiveDisplayModeRegistryModule())
                .registerModule(Visibility.class, new VisibilityRegistryModule())
                .registerModule(new ArgumentTypeRegistryModule())
                .registerModule(ChatType.class, new ChatTypeRegistryModule())
                .registerModule(ChatVisibility.class, ChatVisibilityRegistryModule.get())
                .registerModule(new SelectorFactoryRegistryModule())
                .registerModule(SelectorType.class, SelectorTypeRegistryModule.INSTANCE)
                .registerModule(TextColor.class, TextColorRegistryModule.INSTANCE)
                .registerModule(TextSerializerRegistryModule.INSTANCE)
                .registerModule(TextStyle.Base.class, TextStyleRegistryModule.INSTANCE)
                .registerModule(TranslationManagerRegistryModule.INSTANCE)
                .registerModule(BanType.class, new BanTypeRegistryModule())
                .registerModule(Rotation.class, RotationRegistryModule.get())
                .registerModule(BiomeType.class, BiomeRegistryModule.get())
                .registerModule(new DefaultGameRulesRegistryModule())
                .registerModule(Difficulty.class, DifficultyRegistryModule.get())
                .registerModule(DimensionType.class, new DimensionTypeRegistryModule())
                .registerModule(WorldGeneratorModifier.class, GeneratorModifierRegistryModule.INSTANCE)
                .registerModule(GeneratorType.class, new GeneratorTypeRegistryModule())
                .registerModule(PortalAgentType.class, new PortalAgentTypeRegistryModule())
                .registerModule(SerializationBehavior.class, new SerializationBehaviorRegistryModule())
                .registerModule(Weather.class, new WeatherTypeRegistryModule())
                .registerModule(WorldArchetype.class, new WorldArchetypeRegistryModule())
                .registerModule(EntityType.class, EntityTypeRegistryModule.get())
                .registerModule(TileEntityType.class, TileEntityTypeRegistryModule.get())
                .registerModule(EntityProtocolType.class, new EntityProtocolTypeRegistryModule())
                .registerModule(TileEntityProtocolType.class, new TileEntityProtocolTypeRegistryModule())
                .registerModule(InventoryArchetype.class, new InventoryArchetypeRegistryModule())
                .registerModule(QueryOperationType.class, new QueryOperationRegistryModule())
                .registerModule(new InventoryTransformationRegistryModule())
                .registerModule(GuiId.class, ClientContainerRegistryModule.get())
                .registerModule(GoldenApple.class, GoldenAppleRegistryModule.get())
                .registerModule(CoalType.class, CoalTypeRegistryModule.get())
                .registerModule(Fish.class, FishRegistryModule.get())
                .registerModule(CookedFish.class, CookedFishRegistryModule.get())
                .registerModule(DyeColor.class, DyeColorRegistryModule.get())
                .registerModule(PickupRule.class, PickupRuleRegistryModule.get())
                .registerModule(BannerPatternShape.class, BannerPatternShapeRegistryModule.get())
                .registerModule(EnchantmentType.class, EnchantmentTypeRegistryModule.get())
                .registerModule(SkullType.class, SkullTypeRegistryModule.get())
                .registerModule(PotionType.class, PotionTypeRegistryModule.get())
                .registerModule(RailDirection.class, RailDirectionRegistryModule.get())
                .registerModule(StatisticType.class, StatisticTypeRegistryModule.get())
                .registerModule(Statistic.class, StatisticRegistryModule.get())
                .registerModule(DataRegistration.class, (AdditionalPluginCatalogRegistryModule) DataRegistrationRegistryModule.INSTANCE)
                .registerModule(RecordType.class, RecordTypeRegistryModule.get())
                .registerModule(FluidType.class, FluidTypeRegistryModule.get())
                .registerModule(EventContextKey.class, (AdditionalPluginCatalogRegistryModule) EventContextKeysModule.get())
                .registerModule(new BlockChangeFlagRegistryModule())
                .registerModule(new ItemStackComparatorRegistryModule())
                .registerModule(DataTranslator.class, (AdditionalPluginCatalogRegistryModule) DataTranslatorRegistryModule.INSTANCE)
                // Advancements
                .registerModule(AdvancementTree.class, AdvancementTreeRegistryModule.get())
                .registerModule(Advancement.class, AdvancementRegistryModule.get())
                .registerModule(AdvancementType.class, new AdvancementTypeRegistryModule())
                .registerModule(Trigger.class, (AdditionalPluginCatalogRegistryModule) TriggerRegistryModule.get())
                .registerModule(new AdvancementTreeLayoutModule())
                .registerModule(new AdvancementCriterionModule())
                // Recipes
                .registerModule(CraftingRecipe.class, this.craftingRecipeRegistry)
                .registerModule(SmeltingRecipe.class, this.smeltingRecipeRegistry)
                .registerModule(IFuel.class, this.fuelRegistryModule)
                // Script registry modules
                .registerModule(Parameter.class, new ContextParameterRegistryModule())
                .registerModule(ActionType.class, ActionTypeRegistryModule.get())
                .registerModule(ConditionType.class, ConditionTypeRegistryModule.get())
                .registerModule(DoubleValueProviderType.class, DoubleValueProviderTypeRegistryModule.get())
                .registerModule(FloatValueProviderType.class, FloatValueProviderTypeRegistryModule.get())
                .registerModule(IntValueProviderType.class, IntValueProviderTypeRegistryModule.get())
                ;
        registerFactories();
    }

    private void registerFactories() {
        final List<FactoryRegistry<?, ?>> factoryRegistries = new ArrayList<>();
        factoryRegistries.add(new ResourcePackFactoryModule());
        factoryRegistries.add(new TimingsFactoryRegistryModule());

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

    @Override
    public CatalogKey resolveKey(String value) {
        final int index = value.indexOf(':');
        if (index == -1) {
            return new LanternCatalogKey(MINECRAFT_NAMESPACE, value);
        }
        final String namespace = value.substring(0, index);
        final String actualValue = value.substring(index + 1);
        return new LanternCatalogKey(namespace, actualValue);
    }

    @Deprecated
    @Override
    public <T extends CatalogType> Optional<T> getType(Class<T> typeClass, String id) {
        return getType(typeClass, CatalogKeys.resolve(id));
    }

    @Override
    public <T extends CatalogType> Optional<T> getType(Class<T> typeClass, CatalogKey key) {
        final CatalogRegistryModule<T> registryModule = this.getCatalogRegistryModule(typeClass).orElse(null);
        if (registryModule == null) {
            return Optional.empty();
        } else {
            return registryModule.get(key);
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
        final CatalogRegistryModule<T> registryModule = getCatalogRegistryModule(typeClass).orElse(null);
        if (registryModule == null) {
            return Collections.emptyList();
        } else {
            return registryModule.getAll().stream()
                    .filter(type -> pluginId.equals(type.getKey().getNamespace()))
                    .collect(ImmutableList.toImmutableList());
        }
    }

    @Override
    public <T extends CatalogType> Collection<T> getAllForMinecraft(Class<T> typeClass) {
        return getAllFor(InternalPluginsInfo.Minecraft.IDENTIFIER, typeClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ResettableBuilder<?, ? super T>> T createBuilder(Class<T> builderClass) throws IllegalArgumentException {
        checkNotNull(builderClass, "Builder class was null!");
        final Supplier<?> supplier = this.builderSupplierMap.get(builderClass);
        if (supplier == null) {
            throw new IllegalArgumentException("Could not find a Supplier for the provided class: " + builderClass.getCanonicalName());
        }
        return (T) supplier.get();
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
        registerModulePhase();
    }

    public void preRegistry() {
        this.phase = RegistrationPhase.PRE_REGISTRY;
        registerModulePhase();
    }

    public void preInit() {
        this.phase = RegistrationPhase.PRE_INIT;
        registerModulePhase();
    }

    @SuppressWarnings("unchecked")
    public void init() {
        DataRegistrar.setupRegistrations(this.game);
        this.phase = RegistrationPhase.INIT;
        registerModulePhase();
    }

    public void postInit() {
        DataRegistrar.finalizeRegistrations(this.game);
        this.phase = RegistrationPhase.POST_INIT;
        registerModulePhase();
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
            addToGraph(aModule, graph);
        }
        // Now we need ot do the catalog ones
        for (CatalogRegistryModule<?> aModule : this.catalogRegistryMap.values()) {
            if (!this.classMap.containsKey(aModule.getClass())) {
                this.classMap.put(aModule.getClass(), aModule);
            }
            addToGraph(aModule, graph);
        }
        this.orderedModules.clear();
        try {
            this.orderedModules.addAll(TopologicalOrder.createOrderedLoad(graph));
        } catch (CyclicGraphException e) {
            final StringBuilder msg = new StringBuilder();
            msg.append("Registry module dependencies are cyclical!\n");
            msg.append("Dependency loops are:\n");
            for (DirectedGraph.DataNode<?>[] cycle : e.getCycles()) {
                msg.append("[");
                for (DirectedGraph.DataNode<?> node : cycle) {
                    msg.append(node.getData().toString()).append(" ");
                }
                msg.append("]\n");
            }
            this.game.getLog4jLogger().fatal(msg.toString());
            throw new RuntimeException("Registry modules dependencies error.");
        }
        this.modulesSynced = true;
    }

    private void tryModulePhaseRegistration(RegistryModule module) {
        try {
            final Set<Method> methods = getCustomRegistrations(module);
            methods.stream().filter(this::isProperPhase).forEach(method -> invokeCustomRegistration(module, method));
            if (isProperPhase(module)) {
                module.registerDefaults();
                for (CatalogMappingData data : getCatalogMappingData(module)) {
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
            return isProperPhase(module.getClass().getMethod("registerDefaults"));
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

    @SuppressWarnings("unchecked")
    private void registerModulePhase() {
        syncModules();
        for (Class<? extends RegistryModule> moduleClass : this.orderedModules) {
            if (!this.classMap.containsKey(moduleClass)) {
                throw new IllegalStateException("Something funky happened! The module "
                        + moduleClass + " is required but seems to be missing.");
            }
            tryModulePhaseRegistration(this.classMap.get(moduleClass));
            if (this.phase == RegistrationPhase.INIT) {
                Map.Entry<Class<? extends CatalogType>, CatalogRegistryModule<?>> selectedEntry = null;
                for (Map.Entry<Class<? extends CatalogType>, CatalogRegistryModule<?>> entry : this.catalogRegistryMap.entrySet()) {
                    if (entry.getValue().getClass() == moduleClass) {
                        selectedEntry = entry;
                        break;
                    }
                }
                if (selectedEntry == null) {
                    continue;
                }
                final CatalogRegistryModule module = selectedEntry.getValue();
                if (module instanceof AdditionalCatalogRegistryModule &&
                        module.getClass().getAnnotation(CustomRegistrationPhase.class) == null) {
                    this.game.getEventManager().post(new LanternGameRegistryRegisterEvent(CauseStack.current().getCurrentCause(),
                            selectedEntry.getKey(), (AdditionalCatalogRegistryModule) module));
                }
            }
        }
        registerAdditionalPhase();
    }

    private void registerAdditionalPhase() {
        for (Class<? extends RegistryModule> moduleClass : this.orderedModules) {
            final RegistryModule module = this.classMap.get(moduleClass);
            //RegistryModuleLoader.tryAdditionalRegistration(module);
        }
    }

    private void addToGraph(RegistryModule module, DirectedGraph<Class<? extends RegistryModule>> graph) {
        graph.add(module.getClass());
        final RegistrationDependency dependency = module.getClass().getAnnotation(RegistrationDependency.class);
        if (dependency != null) {
            for (Class<? extends RegistryModule> dependent : dependency.value()) {
                graph.addEdge(checkNotNull(module.getClass(), "Dependency class was null!"), dependent);
            }
        }
    }

    /**
     * Gets the {@link AttributeRegistryModule}.
     *
     * @return the attribute registry
     */
    public AttributeRegistryModule getAttributeRegistry() {
        return getRegistryModule(AttributeRegistryModule.class).get();
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
        return getRegistryModule(TranslationManagerRegistryModule.class).get().getTranslationManager();
    }

    public LanternAttributeCalculator getAttributeCalculator() {
        return this.attributeCalculator;
    }

    @Override
    public Collection<String> getDefaultGameRules() {
        return this.getRegistryModule(DefaultGameRulesRegistryModule.class).get().get();
    }

    @Override
    public Optional<EntityStatistic> getEntityStatistic(StatisticType statType, EntityType entityType) {
        return null;
    }

    @Override
    public Optional<ItemStatistic> getItemStatistic(StatisticType statType, ItemType itemType) {
        return null;
    }

    @Override
    public Optional<BlockStatistic> getBlockStatistic(StatisticType statType, BlockType blockType) {
        return null;
    }

    @Override
    public Optional<Rotation> getRotationFromDegree(int degrees) {
        return RotationRegistryModule.get().getRotationFromDegree(degrees);
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
    public ICraftingRecipeRegistry getCraftingRecipeRegistry() {
        return this.craftingRecipeRegistry;
    }

    @Override
    public SmeltingRecipeRegistry getSmeltingRecipeRegistry() {
        return this.smeltingRecipeRegistry;
    }

    /**
     * Gets the {@link IFuelRegistry}.
     *
     * @return The fuel registry
     */
    public IFuelRegistry getFuelRegistry() {
        return this.fuelRegistryModule;
    }

    @Override
    public Optional<ResourcePack> getResourcePackById(String id) {
        return this.resourcePackFactory.getById(id);
    }

    @Override
    public Optional<DisplaySlot> getDisplaySlotForColor(TextColor color) {
        return getRegistryModule(DisplaySlotRegistryModule.class).get().getByTeamColor(color);
    }

    @Override
    public Optional<Translation> getTranslationById(String id) {
        return getTranslationManager().getIfPresent(id);
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
        return LanternValueFactory.get();
    }

    @Override
    public VillagerRegistry getVillagerRegistry() {
        return null;
    }

    @Override
    public XTextFactory getTextFactory() {
        return LanternTextFactory.INSTANCE;
    }

    @Deprecated
    @Override
    public LanternSelectorFactory getSelectorFactory() {
        return getRegistryModule(SelectorFactoryRegistryModule.class).get().getFactory();
    }

    @Override
    public Locale getLocale(String locale) {
        return LocaleCache.get(locale);
    }

    @Override
    public ShardRegistry getShardRegistry() {
        return LanternShardRegistry.INSTANCE;
    }
}
