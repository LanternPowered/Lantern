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
package org.lanternpowered.server.registry.type.effect.particle

import org.lanternpowered.api.effect.firework.FireworkEffect
import org.lanternpowered.api.registry.CatalogTypeRegistry
import org.lanternpowered.api.registry.CatalogTypeRegistryBuilder
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.effect.particle.LanternParticleOption
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.data.type.NotePitch
import org.spongepowered.api.effect.particle.ParticleOption
import org.spongepowered.api.effect.potion.PotionEffectType
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.util.Color
import org.spongepowered.api.util.Direction
import org.spongepowered.math.vector.Vector3d

val ParticleOptionRegistry: CatalogTypeRegistry<ParticleOption<*>> = catalogTypeRegistry {
    register<BlockState>("block_state")
    register<Color>("color")
    register<Direction>("direction")
    register<List<FireworkEffect>>("firework_effects") { value -> check(value.isEmpty()) { "The firework effects list may not be empty" } }
    register<Int>("quantity") { value -> check(value < 1) { "Quantity must be at least 1" } }
    register<ItemStackSnapshot>("item_stack_snapshot")
    register<NotePitch>("note")
    register<Vector3d>("offset")
    register<PotionEffectType>("potion_effect_type")
    register<Double>("scale") { value -> check(value < 0) { "Scale may not be negative" } }
    register<Vector3d>("velocity")
    register<Boolean>("slow_horizontal_velocity")
}

private inline fun <reified V> CatalogTypeRegistryBuilder<ParticleOption<*>>.register(
        id: String, noinline valueValidator: (V) -> Unit = {}
): ParticleOption<*> = register(LanternParticleOption(ResourceKey.minecraft(id), V::class.java, valueValidator))
