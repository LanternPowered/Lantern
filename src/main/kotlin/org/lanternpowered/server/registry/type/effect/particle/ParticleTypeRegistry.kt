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

import org.lanternpowered.api.effect.firework.fireworkEffect
import org.lanternpowered.api.item.inventory.itemStackOf
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.api.util.collections.immutableMapBuilderOf
import org.lanternpowered.server.effect.particle.LanternParticleType
import org.lanternpowered.server.game.registry.InternalRegistries
import org.lanternpowered.server.inventory.LanternItemStack
import org.spongepowered.api.block.BlockTypes
import org.spongepowered.api.data.type.NotePitches
import org.spongepowered.api.effect.particle.ParticleOption
import org.spongepowered.api.effect.particle.ParticleOptions
import org.spongepowered.api.effect.particle.ParticleType
import org.spongepowered.api.effect.potion.PotionEffectTypes
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.util.Color
import org.spongepowered.api.util.Direction
import org.spongepowered.math.vector.Vector3d
import java.util.function.Supplier

val ParticleTypeRegistry = catalogTypeRegistry<ParticleType> {
    fun registerEffect(key: NamespacedKey, internalType: Int?, options: Map<Supplier<out ParticleOption<*>>, Any>) =
            register(LanternParticleType(key, internalType, options.mapKeys { (key, _) -> key.get() }))

    fun registerEffect(id: String, internalType: Int?, options: Map<Supplier<out ParticleOption<*>>, Any>) =
            registerEffect(minecraftKey(id), internalType, options)

    fun registerEffect(id: String, vararg options: Pair<Supplier<out ParticleOption<*>>, Any>) =
            registerEffect(id, null, options.toMap())

    val particleIds = InternalRegistries.load("particle_type")

    fun registerParticle(id: String, velocity: Boolean, vararg extraOptions: Pair<Supplier<out ParticleOption<*>>, Any>) {
        val options = immutableMapBuilderOf<Supplier<out ParticleOption<*>>, Any>()
        options.put(ParticleOptions.OFFSET, Vector3d.ZERO)
        options.put(ParticleOptions.QUANTITY, 1)
        if (velocity)
            options.put(ParticleOptions.VELOCITY, Vector3d.ZERO)
        options.putAll(extraOptions.toMap())
        registerEffect(id, particleIds.require(minecraftKey(id).formatted), options.build())
    }

    registerParticle("ambient_entity_effect", false,
            ParticleOptions.COLOR to Color.BLACK)
    registerParticle("angry_villager", false)
    registerParticle("barrier", false)
    registerParticle("block", true,
            ParticleOptions.BLOCK_STATE to BlockTypes.STONE.get().defaultState,
            ParticleOptions.ITEM_STACK_SNAPSHOT to itemStackOf(ItemTypes.STONE).createSnapshot())
    registerParticle("bubble", true)
    registerParticle("cloud", true)
    registerParticle("critical_hit", true)
    registerParticle("damage_indicator", true)
    registerParticle("dragon_breath", true)
    registerParticle("dripping_lava", false)
    registerParticle("dripping_water", false)
    registerParticle("dust", true,
            ParticleOptions.COLOR to Color.RED,
            ParticleOptions.SCALE to 1.0)
    registerParticle("effect", true,
            ParticleOptions.SLOW_HORIZONTAL_VELOCITY to false)
    registerParticle("elder_guardian", false)
    registerParticle("enchanted_hit", true)
    registerParticle("enchanting_glyphs", true)
    registerParticle("end_rod", true)
    registerParticle("entity_effect", false,
            ParticleOptions.COLOR to Color.BLACK)
    registerParticle("explosion_emitter", true)
    registerParticle("explosion", false,
            ParticleOptions.SCALE to 1.0)
    registerParticle("falling_dust", true,
            ParticleOptions.BLOCK_STATE to BlockTypes.STONE.get().defaultState,
            ParticleOptions.ITEM_STACK_SNAPSHOT to itemStackOf(ItemTypes.STONE).createSnapshot())
    registerParticle("fireworks_spark", true)
    registerParticle("fishing", true)
    registerParticle("flame", true)
    registerParticle("happy_villager", true)
    registerParticle("heart", false)
    registerParticle("instant_effect", true,
            ParticleOptions.SLOW_HORIZONTAL_VELOCITY to false)
    registerParticle("item", true,
            ParticleOptions.ITEM_STACK_SNAPSHOT to LanternItemStack(ItemTypes.STONE.get()).createSnapshot())
    registerParticle("item_slime", false)
    registerParticle("item_snowball", false)
    registerParticle("large_smoke", true)
    registerParticle("lava", false)
    registerParticle("mycelium", false)
    registerParticle("note", false,
            ParticleOptions.NOTE to NotePitches.F_SHARP0)
    registerParticle("snowball_poof", true)
    registerParticle("portal", true)
    registerParticle("rain_splash", true)
    registerParticle("smoke", true)
    registerParticle("spit", true)
    registerParticle("squid_ink", true)
    registerParticle("sweep_attack", false,
            ParticleOptions.SCALE to 1.0)
    registerParticle("totem_of_undying", true)
    registerParticle("underwater", false)
    registerParticle("water_splash", false)
    registerParticle("witch_magic", true,
            ParticleOptions.SLOW_HORIZONTAL_VELOCITY to false)
    registerParticle("bubble_pop", true)
    registerParticle("current_down", false)
    registerParticle("bubble_column_up", true)
    registerParticle("nautilus", true)
    registerParticle("dolphin_speed", false)

    // Extra effect types
    registerEffect("break_block",
            ParticleOptions.BLOCK_STATE to BlockTypes.STONE.get().defaultState,
            ParticleOptions.ITEM_STACK_SNAPSHOT to itemStackOf(ItemTypes.STONE).createSnapshot())
    registerEffect("break_eye_of_ender")
    registerEffect("break_splash_potion",
            ParticleOptions.POTION_EFFECT_TYPE to PotionEffectTypes.NIGHT_VISION)
    registerEffect("dragon_breath_attack")
    registerEffect("fertilizer",
            ParticleOptions.QUANTITY to 15)
    registerEffect("fireworks",
            ParticleOptions.FIREWORK_EFFECTS to listOf(fireworkEffect { color(Color.BLACK) }))
    registerEffect("fire_smoke",
            ParticleOptions.DIRECTION to Direction.UP)
    registerEffect("mobspawner_flames")
}
