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

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.cause.CauseStackManager
import org.lanternpowered.api.data.KeyBuilder
import org.lanternpowered.api.effect.firework.FireworkEffectBuilder
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.registry.GameRegistry
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.api.scoreboard.ScoreboardBuilder
import org.lanternpowered.api.scoreboard.ScoreboardObjectiveBuilder
import org.lanternpowered.api.scoreboard.ScoreboardTeam
import org.lanternpowered.api.scoreboard.ScoreboardTeamBuilder
import org.lanternpowered.server.data.key.SpongeValueKeyBuilder
import org.lanternpowered.server.data.key.ValueKeyBuilder
import org.lanternpowered.server.data.manipulator.ImmutableDataManipulatorFactory
import org.lanternpowered.server.data.manipulator.MutableDataManipulatorFactory
import org.lanternpowered.server.data.value.BoundedValueFactory
import org.lanternpowered.server.data.value.ValueFactory
import org.lanternpowered.server.effect.firework.LanternFireworkEffectBuilder
import org.lanternpowered.server.item.ItemStackComparatorsRegistry
import org.lanternpowered.server.registry.type.advancement.AdvancementRegistry
import org.lanternpowered.server.registry.type.advancement.AdvancementTreeRegistry
import org.lanternpowered.server.registry.type.advancement.AdvancementTriggerRegistry
import org.lanternpowered.server.registry.type.advancement.AdvancementTypeRegistry
import org.lanternpowered.server.registry.type.boss.BossBarColorRegistry
import org.lanternpowered.server.registry.type.boss.BossBarOverlayRegistry
import org.lanternpowered.server.registry.type.data.ArtTypeRegistry
import org.lanternpowered.server.registry.type.data.BannerPatternShapeRegistry
import org.lanternpowered.server.registry.type.data.BedPartRegistry
import org.lanternpowered.server.registry.type.data.CatTypeRegistry
import org.lanternpowered.server.registry.type.data.ChestAttachmentTypeRegistry
import org.lanternpowered.server.registry.type.data.ComparatorTypeRegistry
import org.lanternpowered.server.registry.type.data.DismountTypeRegistry
import org.lanternpowered.server.registry.type.data.DoorHalfRegistry
import org.lanternpowered.server.registry.type.data.DyeColorRegistry
import org.lanternpowered.server.registry.type.data.FireworkShapeRegistry
import org.lanternpowered.server.registry.type.data.HandPreferenceRegistry
import org.lanternpowered.server.registry.type.data.HandTypeRegistry
import org.lanternpowered.server.registry.type.data.HingeRegistry
import org.lanternpowered.server.registry.type.data.HorseColorRegistry
import org.lanternpowered.server.registry.type.data.HorseStyleRegistry
import org.lanternpowered.server.registry.type.data.InstrumentTypeRegistry
import org.lanternpowered.server.registry.type.data.LlamaTypeRegistry
import org.lanternpowered.server.registry.type.data.MusicDiscRegistry
import org.lanternpowered.server.registry.type.data.NotePitchRegistry
import org.lanternpowered.server.registry.type.data.PickupRuleRegistry
import org.lanternpowered.server.registry.type.data.PortionTypeRegistry
import org.lanternpowered.server.registry.type.data.ProfessionRegistry
import org.lanternpowered.server.registry.type.data.RabbitTypeRegistry
import org.lanternpowered.server.registry.type.data.RailDirectionRegistry
import org.lanternpowered.server.registry.type.data.RecipeTypeRegistry
import org.lanternpowered.server.registry.type.data.SkinPartRegistry
import org.lanternpowered.server.registry.type.data.SlabPortionRegistry
import org.lanternpowered.server.registry.type.data.SpawnTypeRegistry
import org.lanternpowered.server.registry.type.data.SurfaceRegistry
import org.lanternpowered.server.registry.type.data.TeleportTypeRegistry
import org.lanternpowered.server.registry.type.data.VillagerTypeRegistry
import org.lanternpowered.server.registry.type.data.WireAttachmentTypeRegistry
import org.lanternpowered.server.registry.type.data.WoodTypeRegistry
import org.lanternpowered.server.registry.type.economy.TransactionTypeRegistry
import org.lanternpowered.server.registry.type.effect.particle.ParticleOptionRegistry
import org.lanternpowered.server.registry.type.effect.sound.SoundCategoryRegistry
import org.lanternpowered.server.registry.type.effect.sound.SoundTypeRegistry
import org.lanternpowered.server.registry.type.fluid.FluidTypeRegistry
import org.lanternpowered.server.registry.type.inventory.EquipmentTypeRegistry
import org.lanternpowered.server.registry.type.scoreboard.CollisionRuleRegistry
import org.lanternpowered.server.registry.type.scoreboard.CriterionRegistry
import org.lanternpowered.server.registry.type.scoreboard.DisplaySlotRegistry
import org.lanternpowered.server.registry.type.scoreboard.ObjectiveDisplayModeRegistry
import org.lanternpowered.server.registry.type.scoreboard.VisibilityRegistry
import org.lanternpowered.server.registry.type.text.ChatTypeRegistry
import org.lanternpowered.server.registry.type.text.ChatVisibilityRegistry
import org.lanternpowered.server.registry.type.text.TextColorRegistry
import org.lanternpowered.server.registry.type.text.TextSerializerRegistry
import org.lanternpowered.server.registry.type.util.BanTypeRegistry
import org.lanternpowered.server.registry.type.world.DifficultyRegistry
import org.lanternpowered.server.registry.type.world.PortalAgentTypeRegistry
import org.lanternpowered.server.registry.type.world.SerializationBehaviorRegistry
import org.lanternpowered.server.scheduler.LanternTaskBuilder
import org.lanternpowered.server.scoreboard.LanternObjectiveBuilder
import org.lanternpowered.server.scoreboard.LanternScoreboardBuilder
import org.lanternpowered.server.scoreboard.LanternTeam
import org.lanternpowered.server.scoreboard.LanternTeamBuilder
import org.lanternpowered.server.text.LanternTextFactory
import org.lanternpowered.server.text.LanternTextSerializerFactory
import org.lanternpowered.server.text.LanternTextTemplateFactory
import org.lanternpowered.server.timings.DummyTimingsFactory
import org.lanternpowered.server.world.LanternWorldBorderBuilder
import org.spongepowered.api.CatalogType
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.registry.RegistryEvent
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.util.ResettableBuilder
import org.spongepowered.api.world.WorldBorder
import java.util.function.Supplier

class LanternGameRegistry : GameRegistry {

    fun init() {
        factoryRegistry.apply {
            register(ImmutableDataManipulatorFactory)
            register(MutableDataManipulatorFactory)
            register(ValueFactory)
            register(BoundedValueFactory)

            register(ItemStackComparatorsRegistry)

            register(LanternTextFactory)
            register(LanternTextSerializerFactory)
            register(LanternTextTemplateFactory)

            register(DummyTimingsFactory)
        }

        builderRegistry.apply {
            register<Key.Builder<Any, Value<Any>>> { SpongeValueKeyBuilder() }
            register<KeyBuilder<Value<Any>>> { ValueKeyBuilder() }

            register<FireworkEffectBuilder> { LanternFireworkEffectBuilder() }

            register<Task.Builder> { LanternTaskBuilder() }

            register<ScoreboardBuilder> { LanternScoreboardBuilder() }
            register<ScoreboardObjectiveBuilder> { LanternObjectiveBuilder() }
            register<ScoreboardTeamBuilder> { LanternTeamBuilder() }

            register<WorldBorder.Builder> { LanternWorldBorderBuilder() }
        }

        catalogRegistry.apply {
            register(AdvancementRegistry)
            register(AdvancementTreeRegistry)
            register(AdvancementTriggerRegistry)
            register(AdvancementTypeRegistry)

            register(BossBarColorRegistry)
            register(BossBarOverlayRegistry)

            register(ArtTypeRegistry)
            register(BannerPatternShapeRegistry)
            register(CatTypeRegistry)
            register(DismountTypeRegistry)
            register(FireworkShapeRegistry)
            register(HandPreferenceRegistry)
            register(HandTypeRegistry)
            register(HorseColorRegistry)
            register(HorseStyleRegistry)
            register(LlamaTypeRegistry)
            register(MusicDiscRegistry)
            register(NotePitchRegistry)
            register(PickupRuleRegistry)
            register(ProfessionRegistry)
            register(RabbitTypeRegistry)
            register(RecipeTypeRegistry)
            register(SkinPartRegistry)
            register(SpawnTypeRegistry)
            register(TeleportTypeRegistry)
            register(VillagerTypeRegistry)
            register(WoodTypeRegistry)

            register(BedPartRegistry)
            register(ChestAttachmentTypeRegistry)
            register(ComparatorTypeRegistry)
            register(DoorHalfRegistry)
            register(DyeColorRegistry)
            register(HingeRegistry)
            register(InstrumentTypeRegistry)
            register(PortionTypeRegistry)
            register(RailDirectionRegistry)
            register(SlabPortionRegistry)
            register(SurfaceRegistry)
            register(WireAttachmentTypeRegistry)

            register(TransactionTypeRegistry)

            register(ParticleOptionRegistry)

            register(SoundCategoryRegistry)
            register(SoundTypeRegistry)

            register(FluidTypeRegistry)

            register(EquipmentTypeRegistry)

            register(CollisionRuleRegistry)
            register(CriterionRegistry)
            register(DisplaySlotRegistry)
            register(ObjectiveDisplayModeRegistry)
            register(VisibilityRegistry)

            register(ChatTypeRegistry)
            register(ChatVisibilityRegistry)
            register(TextColorRegistry)
            register(TextSerializerRegistry)

            register(BanTypeRegistry)

            register(DifficultyRegistry)
            register(PortalAgentTypeRegistry)
            register(SerializationBehaviorRegistry)
        }

        // Allow plugins to register their catalog type, builders and factories
        postBuilderRegistryEvent()
        postCatalogRegistryEvent()

        // Load all the catalog types
        catalogRegistry.ensureLoaded()
    }

    private fun postBuilderRegistryEvent() {
        val cause = CauseStackManager.currentCause
        val builderRegistryEvent = object : RegistryEvent.Builder {
            override fun getCause(): Cause = cause
            override fun <T : ResettableBuilder<*, in T>> register(builderClass: Class<T>, supplier: Supplier<in T>) =
                    builderRegistry.register(builderClass, supplier)
        }
        EventManager.post(builderRegistryEvent)
    }

    private fun postCatalogRegistryEvent() {
        val cause = CauseStackManager.currentCause
        val catalogRegistryEvent = object : RegistryEvent.CatalogRegistry {
            override fun getCause(): Cause = cause
            override fun <T : CatalogType> register(catalogClass: Class<T>) = register(catalogClass) { emptySet() }
            override fun <T : CatalogType> register(catalogClass: Class<T>, defaultsSupplier: Supplier<Set<T>>) {
                val registry = catalogTypeRegistry<T>(TypeToken.of(catalogClass)) {
                    allowPluginRegistrations()
                    val defaults = defaultsSupplier.get()
                    for (def in defaults)
                        register(def)
                }
                catalogRegistry.register(registry)
            }
        }
        EventManager.post(catalogRegistryEvent)
    }

    override fun getBuilderRegistry() = LanternBuilderRegistry
    override fun getCatalogRegistry() = LanternCatalogRegistry
    override fun getFactoryRegistry() = LanternFactoryRegistry
    override fun getRecipeRegistry() = LanternRecipeRegistry
    override fun getVillagerRegistry() = LanternVillagerRegistry
}
