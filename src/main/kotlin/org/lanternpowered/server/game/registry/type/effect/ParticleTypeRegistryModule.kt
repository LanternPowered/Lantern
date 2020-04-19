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
package org.lanternpowered.server.game.registry.type.effect

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import org.lanternpowered.server.effect.particle.LanternParticleType
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule
import org.lanternpowered.server.game.registry.InternalRegistries
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule
import org.lanternpowered.server.game.registry.type.data.NotePitchRegistryModule
import org.lanternpowered.server.game.registry.type.item.FireworkShapeRegistryModule
import org.lanternpowered.server.inventory.LanternItemStack
import org.lanternpowered.server.item.ItemTypeRegistry
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.block.BlockTypes
import org.spongepowered.api.data.type.NotePitches
import org.spongepowered.api.effect.particle.ParticleOption
import org.spongepowered.api.effect.particle.ParticleOptions
import org.spongepowered.api.effect.particle.ParticleType
import org.spongepowered.api.effect.particle.ParticleTypes
import org.spongepowered.api.effect.potion.PotionEffectTypes
import org.spongepowered.api.item.FireworkEffect
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.registry.util.RegistrationDependency
import org.spongepowered.api.util.Color
import org.spongepowered.api.util.Direction
import org.spongepowered.math.vector.Vector3d
import java.util.OptionalInt

@RegistrationDependency(
        ParticleOptionRegistryModule::class,
        NotePitchRegistryModule::class,
        BlockRegistryModule::class,
        ItemTypeRegistry::class,
        PotionEffectTypeRegistryModule::class,
        FireworkShapeRegistryModule::class
)
class ParticleTypeRegistryModule : DefaultCatalogRegistryModule<ParticleType>(ParticleTypes::class) {

    private fun registerEffect(id: String, options: Map<ParticleOption<*>, Any>) {
        registerEffect(id, OptionalInt.empty(), options)
    }

    private fun registerEffect(id: String, internalType: OptionalInt, options: Map<ParticleOption<*>, Any>) {
        registerEffect(CatalogKey.minecraft(id), internalType, options)
    }

    private fun registerEffect(key: CatalogKey, internalType: OptionalInt, options: Map<ParticleOption<*>, Any>) {
        register(LanternParticleType(key, if (internalType.isPresent) internalType.asInt else null, options))
    }

    override fun registerDefaults() {
        val particleIds = InternalRegistries.load("particle_type")

        fun registerParticle(id: String, velocity: Boolean, extraOptions: Map<ParticleOption<*>, Any> = emptyMap()) {
            val key = CatalogKey.minecraft(id)
            val options = ImmutableMap.builder<ParticleOption<*>, Any>()
            options.put(ParticleOptions.OFFSET, Vector3d.ZERO)
            options.put(ParticleOptions.QUANTITY, 1)
            if (velocity) {
                options.put(ParticleOptions.VELOCITY, Vector3d.ZERO)
            }
            options.putAll(extraOptions)
            registerEffect(id, OptionalInt.of(particleIds.require(key.toString())), options.build())
        }

        // TODO: Make these match vanilla ids?

        registerParticle("ambient_entity_effect", false, ImmutableMap.of<ParticleOption<*>, Any>(
                ParticleOptions.COLOR, Color.BLACK))
        registerParticle("angry_villager", false)
        registerParticle("barrier", false)
        registerParticle("block", true, ImmutableMap.of<ParticleOption<*>, Any>(
                ParticleOptions.BLOCK_STATE, BlockTypes.STONE.defaultState,
                ParticleOptions.ITEM_STACK_SNAPSHOT, LanternItemStack(ItemTypes.STONE).createSnapshot()))
        registerParticle("bubble", true)
        registerParticle("cloud", true)
        registerParticle("critical_hit", true)
        registerParticle("damage_indicator", true)
        registerParticle("dragon_breath", true)
        registerParticle("dripping_lava", false)
        registerParticle("dripping_water", false)
        registerParticle("dust", true, ImmutableMap.of(
                ParticleOptions.COLOR, Color.RED,
                ParticleOptions.SCALE, 1.0))
        registerParticle("effect", true, ImmutableMap.of<ParticleOption<*>, Any>(
                ParticleOptions.SLOW_HORIZONTAL_VELOCITY, false))
        registerParticle("elder_guardian", false)
        registerParticle("enchanted_hit", true)
        registerParticle("enchanting_glyphs", true)
        registerParticle("end_rod", true)
        registerParticle("entity_effect", false, ImmutableMap.of<ParticleOption<*>, Any>(
                ParticleOptions.COLOR, Color.BLACK))
        registerParticle("explosion_emitter", true)
        registerParticle("explosion", false, ImmutableMap.of<ParticleOption<*>, Any>(
                ParticleOptions.SCALE, 1.0))
        registerParticle("falling_dust", true, ImmutableMap.of<ParticleOption<*>, Any>(
                ParticleOptions.BLOCK_STATE, BlockTypes.STONE.defaultState,
                ParticleOptions.ITEM_STACK_SNAPSHOT, LanternItemStack(ItemTypes.STONE).createSnapshot()))
        registerParticle("fireworks_spark", true)
        registerParticle("fishing", true)
        registerParticle("flame", true)
        registerParticle("happy_villager", true)
        registerParticle("heart", false)
        registerParticle("instant_effect", true, ImmutableMap.of<ParticleOption<*>, Any>(
                ParticleOptions.SLOW_HORIZONTAL_VELOCITY, false))
        registerParticle("item", true, ImmutableMap.of<ParticleOption<*>, Any>(
                ParticleOptions.ITEM_STACK_SNAPSHOT, LanternItemStack(ItemTypes.STONE).createSnapshot()))
        registerParticle("item_slime", false)
        registerParticle("item_snowball", false)
        registerParticle("large_smoke", true)
        registerParticle("lava", false)
        registerParticle("mycelium", false)
        registerParticle("note", false, ImmutableMap.of<ParticleOption<*>, Any>(
                ParticleOptions.NOTE, NotePitches.F_SHARP0))
        registerParticle("snowball_poof", true)
        registerParticle("portal", true)
        registerParticle("rain_splash", true)
        registerParticle("smoke", true)
        registerParticle("spit", true)
        registerParticle("squid_ink", true)
        registerParticle("sweep_attack", false, ImmutableMap.of<ParticleOption<*>, Any>(
                ParticleOptions.SCALE, 1.0))
        registerParticle("totem_of_undying", true)
        registerParticle("underwater", false)
        registerParticle("water_splash", false)
        registerParticle("witch_magic", true, ImmutableMap.of<ParticleOption<*>, Any>(
                ParticleOptions.SLOW_HORIZONTAL_VELOCITY, false))
        registerParticle("bubble_pop", true)
        registerParticle("current_down", false)
        registerParticle("bubble_column_up", true)
        registerParticle("nautilus", true)
        registerParticle("dolphin_speed", false)

        // Extra effect types
        registerEffect("break_block", ImmutableMap.of<ParticleOption<*>, Any>(
                ParticleOptions.BLOCK_STATE, BlockTypes.STONE.defaultState,
                ParticleOptions.ITEM_STACK_SNAPSHOT, LanternItemStack(ItemTypes.STONE).createSnapshot()))
        registerEffect("break_eye_of_ender", ImmutableMap.of())
        registerEffect("break_splash_potion", ImmutableMap.of<ParticleOption<*>, Any>(
                ParticleOptions.POTION_EFFECT_TYPE, PotionEffectTypes.NIGHT_VISION))
        registerEffect("dragon_breath_attack", ImmutableMap.of())
        registerEffect("fertilizer", ImmutableMap.of<ParticleOption<*>, Any>(
                ParticleOptions.QUANTITY, 15))
        registerEffect("fireworks", ImmutableMap.of<ParticleOption<*>, Any>(
                ParticleOptions.FIREWORK_EFFECTS, ImmutableList.of(
                FireworkEffect.builder().color(Color.BLACK).build())))
        registerEffect("fire_smoke", ImmutableMap.of<ParticleOption<*>, Any>(
                ParticleOptions.DIRECTION, Direction.UP))
        registerEffect("mobspawner_flames", ImmutableMap.of())
    }
}
