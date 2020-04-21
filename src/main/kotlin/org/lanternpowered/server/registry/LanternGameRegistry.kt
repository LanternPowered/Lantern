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
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.registry.BuilderRegistry
import org.lanternpowered.api.registry.GameRegistry
import org.lanternpowered.api.registry.RecipeRegistry
import org.lanternpowered.api.registry.VillagerRegistry
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.registry.type.advancement.AdvancementRegistry
import org.lanternpowered.server.registry.type.advancement.AdvancementTreeRegistry
import org.lanternpowered.server.registry.type.advancement.AdvancementTriggerRegistry
import org.lanternpowered.server.registry.type.advancement.AdvancementTypeRegistry
import org.lanternpowered.server.registry.type.boss.BossBarColorRegistry
import org.lanternpowered.server.registry.type.boss.BossBarOverlayRegistry
import org.lanternpowered.server.registry.type.data.ArtTypeRegistry
import org.lanternpowered.server.registry.type.data.BannerPatternShapeRegistry
import org.lanternpowered.server.registry.type.data.CatTypeRegistry
import org.lanternpowered.server.registry.type.data.FireworkShapeRegistry
import org.lanternpowered.server.registry.type.data.HandPreferenceRegistry
import org.lanternpowered.server.registry.type.data.HandTypeRegistry
import org.lanternpowered.server.registry.type.data.HorseColorRegistry
import org.lanternpowered.server.registry.type.data.HorseStyleRegistry
import org.lanternpowered.server.registry.type.data.LlamaTypeRegistry
import org.lanternpowered.server.registry.type.data.MusicDiscRegistry
import org.lanternpowered.server.registry.type.data.NotePitchRegistry
import org.lanternpowered.server.registry.type.data.PickupRuleRegistry
import org.lanternpowered.server.registry.type.data.ProfessionRegistry
import org.lanternpowered.server.registry.type.data.RabbitTypeRegistry
import org.lanternpowered.server.registry.type.data.SkinPartRegistry
import org.lanternpowered.server.registry.type.data.VillagerTypeRegistry
import org.lanternpowered.server.registry.type.data.WoodTypeRegistry
import org.lanternpowered.server.registry.type.economy.TransactionTypeRegistry
import org.lanternpowered.server.registry.type.effect.sound.SoundCategoryRegistry
import org.lanternpowered.server.registry.type.effect.sound.SoundTypeRegistry
import org.lanternpowered.server.registry.type.scoreboard.CollisionRuleRegistry
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
import org.spongepowered.api.CatalogType
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.registry.RegistryEvent
import org.spongepowered.api.util.ResettableBuilder
import java.util.function.Supplier

class LanternGameRegistry : GameRegistry {

    fun init() {
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
            register(SkinPartRegistry)
            register(VillagerTypeRegistry)
            register(WoodTypeRegistry)

            register(TransactionTypeRegistry)

            register(SoundCategoryRegistry)
            register(SoundTypeRegistry)

            register(CollisionRuleRegistry)
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
            override fun <T : ResettableBuilder<*, in T>> register(builderClass: Class<T>, supplier: Supplier<in T>) {
                TODO()
            }
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

    override fun getBuilderRegistry(): BuilderRegistry {
        TODO("Not yet implemented")
    }

    override fun getCatalogRegistry() = LanternCatalogRegistry
    override fun getFactoryRegistry() = LanternFactoryRegistry

    override fun getRecipeRegistry(): RecipeRegistry {
        TODO("Not yet implemented")
    }

    override fun getVillagerRegistry(): VillagerRegistry {
        TODO("Not yet implemented")
    }
}
