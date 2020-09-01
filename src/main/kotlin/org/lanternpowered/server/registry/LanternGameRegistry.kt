/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.registry

import com.google.common.base.CaseFormat
import com.google.common.reflect.TypeToken
import org.lanternpowered.api.Game
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.attribute.AttributeModifierBuilder
import org.lanternpowered.api.attribute.AttributeTypeBuilder
import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.cause.Cause
import org.lanternpowered.api.cause.CauseContextKeyBuilder
import org.lanternpowered.api.cause.CauseStackManager
import org.lanternpowered.api.data.KeyBuilder
import org.lanternpowered.api.effect.firework.FireworkEffectBuilder
import org.lanternpowered.api.effect.potion.PotionEffectBuilder
import org.lanternpowered.api.event.lifecycle.RegisterCatalogRegistryEvent
import org.lanternpowered.api.item.enchantment.EnchantmentTypeBuilder
import org.lanternpowered.api.item.inventory.InventoryTransactionResultBuilder
import org.lanternpowered.api.item.inventory.ItemStackBuilder
import org.lanternpowered.api.registry.CatalogTypeRegistry
import org.lanternpowered.api.registry.GameRegistry
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.api.scoreboard.ScoreboardBuilder
import org.lanternpowered.api.scoreboard.ScoreboardObjectiveBuilder
import org.lanternpowered.api.scoreboard.ScoreboardTeamBuilder
import org.lanternpowered.api.text.placeholder.PlaceholderContextBuilder
import org.lanternpowered.api.text.placeholder.PlaceholderParserBuilder
import org.lanternpowered.api.text.placeholder.PlaceholderTextBuilder
import org.lanternpowered.server.LanternGame
import org.lanternpowered.server.advancement.criteria.AndCriterionFactory
import org.lanternpowered.server.advancement.criteria.CriterionFactory
import org.lanternpowered.server.advancement.criteria.LanternCriterionBuilder
import org.lanternpowered.server.advancement.criteria.LanternScoreCriterionBuilder
import org.lanternpowered.server.advancement.criteria.OrCriterionFactory
import org.lanternpowered.server.attribute.LanternAttributeModifierBuilder
import org.lanternpowered.server.attribute.LanternAttributeTypeBuilder
import org.lanternpowered.server.audience.LanternAudiencesFactory
import org.lanternpowered.server.block.BlockSnapshotBuilder
import org.lanternpowered.server.block.LanternBlockSnapshotBuilder
import org.lanternpowered.server.block.LanternLocatableBlockBuilder
import org.lanternpowered.server.block.entity.LanternBlockEntityArchetypeBuilder
import org.lanternpowered.server.catalog.LanternNamespacedKeyBuilder
import org.lanternpowered.server.command.LanternCommandCauseFactory
import org.lanternpowered.server.config.user.ban.LanternBanBuilder
import org.lanternpowered.server.data.key.SpongeValueKeyBuilder
import org.lanternpowered.server.data.key.ValueKeyBuilder
import org.lanternpowered.server.data.manipulator.ImmutableDataManipulatorFactory
import org.lanternpowered.server.data.manipulator.MutableDataManipulatorFactory
import org.lanternpowered.server.data.value.ValueFactory
import org.lanternpowered.server.effect.entity.EntityEffectCollection
import org.lanternpowered.server.effect.entity.LanternEntityEffectCollectionBuilder
import org.lanternpowered.server.effect.firework.LanternFireworkEffectBuilder
import org.lanternpowered.server.effect.particle.LanternParticleEffectBuilder
import org.lanternpowered.server.effect.potion.LanternPotionEffectBuilder
import org.lanternpowered.server.effect.sound.LanternSoundTypeBuilder
import org.lanternpowered.server.entity.player.tab.LanternTabListEntryBuilder
import org.lanternpowered.server.cause.LanternCauseContextKeyBuilder
import org.lanternpowered.server.fluid.LanternFluidStackBuilder
import org.lanternpowered.server.fluid.LanternFluidStackSnapshotBuilder
import org.lanternpowered.server.inventory.LanternInventoryFactory
import org.lanternpowered.server.inventory.LanternInventoryFilterBuilderFactory
import org.lanternpowered.server.inventory.LanternItemStackBuilder
import org.lanternpowered.server.inventory.container.ContainerTypeRegistry
import org.lanternpowered.server.inventory.transaction.LanternInventoryTransactionResultBuilder
import org.lanternpowered.server.item.ItemStackComparatorsRegistry
import org.lanternpowered.server.item.enchantment.LanternEnchantmentBuilder
import org.lanternpowered.server.item.enchantment.LanternEnchantmentTypeBuilder
import org.lanternpowered.server.registry.type.advancement.AdvancementRegistry
import org.lanternpowered.server.registry.type.advancement.AdvancementTreeRegistry
import org.lanternpowered.server.registry.type.advancement.AdvancementTriggerRegistry
import org.lanternpowered.server.registry.type.advancement.AdvancementTypeRegistry
import org.lanternpowered.server.registry.type.attribute.AttributeOperationRegistry
import org.lanternpowered.server.registry.type.attribute.AttributeTypeRegistry
import org.lanternpowered.server.registry.type.block.BlockEntityTypeRegistry
import org.lanternpowered.server.registry.type.block.BlockStateRegistry
import org.lanternpowered.server.registry.type.block.BlockTypeRegistry
import org.lanternpowered.server.registry.type.cause.CauseContextKeyRegistry
import org.lanternpowered.server.registry.type.cause.DamageModifierTypeRegistry
import org.lanternpowered.server.registry.type.cause.DamageTypeRegistry
import org.lanternpowered.server.registry.type.data.ArmorMaterialRegistry
import org.lanternpowered.server.registry.type.data.ArtTypeRegistry
import org.lanternpowered.server.registry.type.data.AttachmentSurfaceRegistry
import org.lanternpowered.server.registry.type.data.BannerPatternShapeRegistry
import org.lanternpowered.server.registry.type.data.BedPartRegistry
import org.lanternpowered.server.registry.type.data.CatTypeRegistry
import org.lanternpowered.server.registry.type.data.ChestAttachmentTypeRegistry
import org.lanternpowered.server.registry.type.data.ComparatorTypeRegistry
import org.lanternpowered.server.registry.type.data.DismountTypeRegistry
import org.lanternpowered.server.registry.type.data.DoorHalfRegistry
import org.lanternpowered.server.registry.type.data.DoorHingeRegistry
import org.lanternpowered.server.registry.type.data.DyeColorRegistry
import org.lanternpowered.server.registry.type.data.FireworkShapeRegistry
import org.lanternpowered.server.registry.type.data.GameModeRegistry
import org.lanternpowered.server.registry.type.data.HandPreferenceRegistry
import org.lanternpowered.server.registry.type.data.HandTypeRegistry
import org.lanternpowered.server.registry.type.data.HorseColorRegistry
import org.lanternpowered.server.registry.type.data.HorseStyleRegistry
import org.lanternpowered.server.registry.type.data.InstrumentTypeRegistry
import org.lanternpowered.server.registry.type.data.LlamaTypeRegistry
import org.lanternpowered.server.registry.type.data.MusicDiscRegistry
import org.lanternpowered.server.registry.type.data.NotePitchRegistry
import org.lanternpowered.server.registry.type.data.PickupRuleRegistry
import org.lanternpowered.server.registry.type.data.PortionTypeRegistry
import org.lanternpowered.server.registry.type.data.ProfessionTypeRegistry
import org.lanternpowered.server.registry.type.data.RabbitTypeRegistry
import org.lanternpowered.server.registry.type.data.RailDirectionRegistry
import org.lanternpowered.server.registry.type.data.SkinPartRegistry
import org.lanternpowered.server.registry.type.data.SlabPortionRegistry
import org.lanternpowered.server.registry.type.data.SpawnTypeRegistry
import org.lanternpowered.server.registry.type.data.TeleportTypeRegistry
import org.lanternpowered.server.registry.type.data.ToolTypeRegistry
import org.lanternpowered.server.registry.type.data.TopHatRegistry
import org.lanternpowered.server.registry.type.data.ValueKeyRegistry
import org.lanternpowered.server.registry.type.data.VillagerTypeRegistry
import org.lanternpowered.server.registry.type.data.WireAttachmentTypeRegistry
import org.lanternpowered.server.registry.type.data.WoodTypeRegistry
import org.lanternpowered.server.registry.type.economy.TransactionTypeRegistry
import org.lanternpowered.server.registry.type.effect.entity.EntityEffectTypeRegistry
import org.lanternpowered.server.registry.type.effect.particle.ParticleOptionRegistry
import org.lanternpowered.server.registry.type.effect.particle.ParticleTypeRegistry
import org.lanternpowered.server.registry.type.effect.sound.SoundTypeRegistry
import org.lanternpowered.server.registry.type.entity.EntityTypeRegistry
import org.lanternpowered.server.registry.type.fluid.FluidTypeRegistry
import org.lanternpowered.server.registry.type.inventory.EquipmentGroupRegistry
import org.lanternpowered.server.registry.type.inventory.EquipmentTypeRegistry
import org.lanternpowered.server.registry.type.item.EnchantmentTypeRegistry
import org.lanternpowered.server.registry.type.item.ItemTypeRegistry
import org.lanternpowered.server.registry.type.network.BlockEntityProtocolTypeRegistry
import org.lanternpowered.server.registry.type.persistence.DataFormatRegistry
import org.lanternpowered.server.registry.type.potion.PotionEffectTypeRegistry
import org.lanternpowered.server.registry.type.potion.PotionTypeRegistry
import org.lanternpowered.server.registry.type.recipe.LanternRecipeRegistry
import org.lanternpowered.server.registry.type.recipe.RecipeTypeRegistry
import org.lanternpowered.server.registry.type.scoreboard.CollisionRuleRegistry
import org.lanternpowered.server.registry.type.scoreboard.ScoreboardCriterionRegistry
import org.lanternpowered.server.registry.type.scoreboard.DisplaySlotRegistry
import org.lanternpowered.server.registry.type.scoreboard.ObjectiveDisplayModeRegistry
import org.lanternpowered.server.registry.type.scoreboard.VisibilityRegistry
import org.lanternpowered.server.registry.type.text.ChatVisibilityRegistry
import org.lanternpowered.server.registry.type.text.TextSerializerRegistry
import org.lanternpowered.server.registry.type.util.BanTypeRegistry
import org.lanternpowered.server.registry.type.util.RotationRegistry
import org.lanternpowered.server.registry.type.world.DifficultyRegistry
import org.lanternpowered.server.registry.type.world.GameRuleRegistry
import org.lanternpowered.server.registry.type.world.PortalAgentTypeRegistry
import org.lanternpowered.server.registry.type.world.SerializationBehaviorRegistry
import org.lanternpowered.server.registry.type.world.UpdatePriorityRegistry
import org.lanternpowered.server.registry.type.world.WorldArchetypeRegistry
import org.lanternpowered.server.resourcepack.LanternResourcePackFactory
import org.lanternpowered.server.scheduler.LanternTaskBuilder
import org.lanternpowered.server.scoreboard.LanternObjectiveBuilder
import org.lanternpowered.server.scoreboard.LanternScoreboardBuilder
import org.lanternpowered.server.scoreboard.LanternTeamBuilder
import org.lanternpowered.server.text.LanternTextSerializerFactory
import org.lanternpowered.server.text.SpongeComponentsFactory
import org.lanternpowered.server.text.placeholder.LanternPlaceholderContextBuilder
import org.lanternpowered.server.text.placeholder.LanternPlaceholderParserBuilder
import org.lanternpowered.server.text.placeholder.LanternPlaceholderTextBuilder
import org.lanternpowered.server.text.placeholder.PlaceholderParserRegistry
import org.lanternpowered.server.timings.DummyTimingsFactory
import org.lanternpowered.server.world.LanternBlockChangeFlag
import org.lanternpowered.server.world.LanternWorldBorderBuilder
import org.lanternpowered.server.world.archetype.LanternWorldArchetypeBuilder
import org.lanternpowered.server.world.biome.LanternVirtualBiomeTypeBuilder
import org.lanternpowered.server.world.gamerule.LanternGameRuleBuilder
import org.spongepowered.api.advancement.criteria.AdvancementCriterion
import org.spongepowered.api.advancement.criteria.ScoreAdvancementCriterion
import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.block.entity.BlockEntityArchetype
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.effect.particle.ParticleEffect
import org.spongepowered.api.effect.sound.SoundType
import org.spongepowered.api.entity.living.player.tab.TabListEntry
import org.spongepowered.api.event.lifecycle.RegisterBuilderEvent
import org.spongepowered.api.event.lifecycle.RegisterFactoryEvent
import org.spongepowered.api.fluid.FluidStack
import org.spongepowered.api.fluid.FluidStackSnapshot
import org.spongepowered.api.item.enchantment.Enchantment
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.service.ban.Ban
import org.spongepowered.api.util.ResettableBuilder
import org.spongepowered.api.util.RespawnLocation
import org.spongepowered.api.world.LocatableBlock
import org.spongepowered.api.world.WorldArchetype
import org.spongepowered.api.world.WorldBorder
import org.spongepowered.api.world.biome.VirtualBiomeType
import org.spongepowered.api.world.gamerule.GameRule
import java.util.function.Supplier
import org.lanternpowered.api.key.NamespacedKey.Builder as NamespacedKeyBuilder

class LanternGameRegistry(
        private val game: LanternGame
) : GameRegistry {

    fun init() {
        factoryRegistry.apply {
            // Advancements
            register(AndCriterionFactory)
            register(CriterionFactory)
            register(OrCriterionFactory)

            // Registries
            register(LanternCatalogTypeRegistryFactory)

            // Data
            register(ImmutableDataManipulatorFactory)
            register(MutableDataManipulatorFactory)
            register(ValueFactory)

            register(LanternCommandCauseFactory)
            register(ItemStackComparatorsRegistry)
            register(LanternResourcePackFactory)

            register(LanternAudiencesFactory(game))

            register(SpongeComponentsFactory)
            register(LanternTextSerializerFactory)

            register(LanternBlockChangeFlag.Factory)

            register(DummyTimingsFactory)

            // Inventory
            register(LanternInventoryFactory)
            register(LanternInventoryFilterBuilderFactory)
        }

        builderRegistry.apply {
            register<AdvancementCriterion.Builder> { LanternCriterionBuilder() }
            register<ScoreAdvancementCriterion.Builder> { LanternScoreCriterionBuilder() }

            register<NamespacedKeyBuilder> { LanternNamespacedKeyBuilder() }
            register<Key.Builder<Any, Value<Any>>> { SpongeValueKeyBuilder() }
            register<KeyBuilder<Value<Any>>> { ValueKeyBuilder() }
            register<CauseContextKeyBuilder<Any>> { LanternCauseContextKeyBuilder() }

            register<BlockSnapshot.Builder> { LanternBlockSnapshotBuilder() }
            register<BlockSnapshotBuilder> { LanternBlockSnapshotBuilder() }
            register<BlockEntityArchetype.Builder> { LanternBlockEntityArchetypeBuilder() }
            register<LocatableBlock.Builder> { LanternLocatableBlockBuilder() }

            register<TabListEntry.Builder> { LanternTabListEntryBuilder() }
            register { RespawnLocation.Builder() }

            register<AttributeModifierBuilder> { LanternAttributeModifierBuilder() }
            register<AttributeTypeBuilder> { LanternAttributeTypeBuilder() }
            register<FireworkEffectBuilder> { LanternFireworkEffectBuilder() }
            register<ParticleEffect.Builder> { LanternParticleEffectBuilder() }
            register<PotionEffectBuilder> { LanternPotionEffectBuilder() }
            register<SoundType.Builder> { LanternSoundTypeBuilder() }
            register<EntityEffectCollection.Builder> { LanternEntityEffectCollectionBuilder() }

            register<FluidStack.Builder> { LanternFluidStackBuilder() }
            register<FluidStackSnapshot.Builder> { LanternFluidStackSnapshotBuilder() }
            register<ItemStackBuilder> { LanternItemStackBuilder() }
            register<Enchantment.Builder> { LanternEnchantmentBuilder() }
            register<EnchantmentTypeBuilder> { LanternEnchantmentTypeBuilder() }
            register<InventoryTransactionResultBuilder> { LanternInventoryTransactionResultBuilder() }

            register<Task.Builder> { LanternTaskBuilder() }

            register<ScoreboardBuilder> { LanternScoreboardBuilder() }
            register<ScoreboardObjectiveBuilder> { LanternObjectiveBuilder() }
            register<ScoreboardTeamBuilder> { LanternTeamBuilder() }

            register<Ban.Builder> { LanternBanBuilder() }

            register<GameRule.Builder<Any>> { LanternGameRuleBuilder() }
            register<VirtualBiomeType.Builder> { LanternVirtualBiomeTypeBuilder() }
            register<WorldArchetype.Builder> { LanternWorldArchetypeBuilder() }
            register<WorldBorder.Builder> { LanternWorldBorderBuilder() }

            register<PlaceholderContextBuilder> { LanternPlaceholderContextBuilder() }
            register<PlaceholderParserBuilder> { LanternPlaceholderParserBuilder() }
            register<PlaceholderTextBuilder> { LanternPlaceholderTextBuilder() }
        }

        catalogRegistry.apply {
            fun register(registry: CatalogTypeRegistry<*>) {
                val value = CaseFormat.UPPER_CAMEL.to(
                        CaseFormat.LOWER_UNDERSCORE, registry.typeToken.rawType.simpleName)
                register(registry, NamespacedKey.minecraft(value))
            }

            register(AdvancementRegistry)
            register(AdvancementTreeRegistry)
            register(AdvancementTriggerRegistry)
            register(AdvancementTypeRegistry)

            register(AttributeOperationRegistry)
            register(AttributeTypeRegistry)

            register(BlockEntityTypeRegistry)
            register(BlockStateRegistry)
            register(BlockTypeRegistry)

            register(CauseContextKeyRegistry)
            register(DamageModifierTypeRegistry)
            register(DamageTypeRegistry)

            register(ArmorMaterialRegistry)
            register(ArtTypeRegistry)
            register(BannerPatternShapeRegistry)
            register(CatTypeRegistry)
            register(DismountTypeRegistry)
            register(FireworkShapeRegistry)
            register(GameModeRegistry)
            register(HandPreferenceRegistry)
            register(HandTypeRegistry)
            register(HorseColorRegistry)
            register(HorseStyleRegistry)
            register(LlamaTypeRegistry)
            register(MusicDiscRegistry)
            register(NotePitchRegistry)
            register(PickupRuleRegistry)
            register(ProfessionTypeRegistry)
            register(RabbitTypeRegistry)
            register(SkinPartRegistry)
            register(SpawnTypeRegistry)
            register(TeleportTypeRegistry)
            register(ToolTypeRegistry)
            register(TopHatRegistry)
            register(ValueKeyRegistry)
            register(VillagerTypeRegistry)
            register(WoodTypeRegistry)

            register(BedPartRegistry)
            register(ChestAttachmentTypeRegistry)
            register(ComparatorTypeRegistry)
            register(DoorHalfRegistry)
            register(DyeColorRegistry)
            register(DoorHingeRegistry)
            register(InstrumentTypeRegistry)
            register(PortionTypeRegistry)
            register(RailDirectionRegistry)
            register(SlabPortionRegistry)
            register(AttachmentSurfaceRegistry)
            register(WireAttachmentTypeRegistry)

            register(TransactionTypeRegistry)

            register(EntityEffectTypeRegistry)

            register(ParticleOptionRegistry)
            register(ParticleTypeRegistry)

            register(SoundTypeRegistry)

            register(EntityTypeRegistry)

            register(FluidTypeRegistry)

            register(EquipmentGroupRegistry)
            register(EquipmentTypeRegistry)

            register(EnchantmentTypeRegistry)
            register(ItemTypeRegistry)
            register(ContainerTypeRegistry)

            register(BlockEntityProtocolTypeRegistry)

            register(DataFormatRegistry)

            register(PotionEffectTypeRegistry)
            register(PotionTypeRegistry)

            register(RecipeTypeRegistry)
            register(LanternRecipeRegistry)

            register(CollisionRuleRegistry)
            register(DisplaySlotRegistry)
            register(ObjectiveDisplayModeRegistry)
            register(ScoreboardCriterionRegistry)
            register(VisibilityRegistry)

            register(ChatVisibilityRegistry)
            register(TextSerializerRegistry)

            register(BanTypeRegistry)
            register(RotationRegistry)

            register(DifficultyRegistry)
            register(GameRuleRegistry)
            register(PortalAgentTypeRegistry)
            register(SerializationBehaviorRegistry)
            register(UpdatePriorityRegistry)
            register(WorldArchetypeRegistry)

            register(PlaceholderParserRegistry)
        }

        // Allow plugins to register their catalog type, builders and factories
        postBuilderRegistryEvent()
        postFactoryRegistryEvent()
        postCatalogRegistryEvent()

        // Load all the catalog types
        catalogRegistry.ensureLoaded()
    }

    private fun postFactoryRegistryEvent() {
        val cause = CauseStackManager.currentCause
        val factoryRegistryEvent = object : RegisterFactoryEvent {
            override fun getCause(): Cause = cause
            override fun getGame(): Game = this@LanternGameRegistry.game
            override fun <T : Any> register(factoryClass: Class<T>, factory: T): T =
                    factoryRegistry.register(factoryClass, factory)
        }
        this.game.eventManager.post(factoryRegistryEvent)
    }

    private fun postBuilderRegistryEvent() {
        val cause = CauseStackManager.currentCause
        val builderRegistryEvent = object : RegisterBuilderEvent {
            override fun getCause(): Cause = cause
            override fun getGame(): Game = this@LanternGameRegistry.game
            override fun <T : ResettableBuilder<*, in T>> register(builderClass: Class<T>, supplier: Supplier<in T>) =
                    builderRegistry.register(builderClass, supplier)
        }
        this.game.eventManager.post(builderRegistryEvent)
    }

    private fun postCatalogRegistryEvent() {
        val cause = CauseStackManager.currentCause
        val catalogRegistryEvent = object : RegisterCatalogRegistryEvent {
            override fun getCause(): Cause = cause
            override fun getGame(): Game = this@LanternGameRegistry.game
            override fun <T : CatalogType> register(catalogClass: Class<T>, key: NamespacedKey): Unit =
                    register(catalogClass, key, null)
            override fun <T : CatalogType> register(catalogClass: Class<T>, key: NamespacedKey, defaultsSupplier: Supplier<Set<T>>?) {
                val registry = catalogTypeRegistry<T>(TypeToken.of(catalogClass)) {
                    allowPluginRegistrations()
                    if (defaultsSupplier != null) {
                        val defaults = defaultsSupplier.get()
                        for (def in defaults)
                            register(def)
                    }
                }
                catalogRegistry.register(registry, key)
            }
            override fun <T : CatalogType> register(registry: CatalogTypeRegistry<T>, key: NamespacedKey): Unit =
                    catalogRegistry.register(registry, key)
        }
        this.game.eventManager.post(catalogRegistryEvent)
    }

    override fun getBuilderRegistry() = LanternBuilderRegistry
    override fun getCatalogRegistry() = LanternCatalogRegistry
    override fun getFactoryRegistry() = LanternFactoryRegistry
    override fun getRecipeRegistry() = LanternRecipeRegistry
    override fun getVillagerRegistry() = LanternVillagerRegistry
    override fun getAdventureRegistry() = LanternAdventureRegistry
}
