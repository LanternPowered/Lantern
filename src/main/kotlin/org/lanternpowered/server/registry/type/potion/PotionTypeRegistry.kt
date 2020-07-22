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
@file:JvmName("PotionTypeRegistry")
package org.lanternpowered.server.registry.type.potion

import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.api.effect.potion.PotionEffectTypes
import org.lanternpowered.api.item.potion.PotionType
import org.lanternpowered.api.item.potion.PotionTypeBuilder
import org.lanternpowered.api.item.potion.potionTypeOf
import org.lanternpowered.api.namespace.minecraftKey
import org.lanternpowered.server.registry.internalCatalogTypeRegistry

@get:JvmName("get")
val PotionTypeRegistry = internalCatalogTypeRegistry<PotionType> {
    fun register(key: NamespacedKey, fn: PotionTypeBuilder.() -> Unit = {}) =
            register(potionTypeOf(key, fn))

    fun register(key: String, fn: PotionTypeBuilder.() -> Unit = {}) =
            register(minecraftKey(key), fn)

    register("empty")
    register("water")
    register("mundane")
    register("thick")
    register("awkward")

    // Night vision
    register("night_vision") {
        addEffect(PotionEffectTypes.NIGHT_VISION, 0, 3600)
    }
    register("long_night_vision") {
        translationKey("night_vision")
        addEffect(PotionEffectTypes.NIGHT_VISION, 0, 9600)
    }

    // Invisibility
    register("invisibility") {
        addEffect(PotionEffectTypes.INVISIBILITY, 0, 3600)
    }
    register("long_invisibility") {
        translationKey("invisibility")
        addEffect(PotionEffectTypes.INVISIBILITY, 0, 9600)
    }

    // Leaping
    register("leaping") {
        addEffect(PotionEffectTypes.JUMP_BOOST, 0, 3600)
    }
    register("long_leaping") {
        translationKey("leaping")
        addEffect(PotionEffectTypes.JUMP_BOOST, 0, 9600)
    }
    register("strong_leaping") {
        translationKey("leaping")
        addEffect(PotionEffectTypes.JUMP_BOOST, 1, 1800)
    }

    // Fire resistance
    register("fire_resistance") {
        addEffect(PotionEffectTypes.FIRE_RESISTANCE, 0, 3600)
    }
    register("long_fire_resistance") {
        translationKey("fire_resistance")
        addEffect(PotionEffectTypes.FIRE_RESISTANCE, 0, 9600)
    }

    // Swiftness
    register("swiftness") {
        addEffect(PotionEffectTypes.SPEED, 0, 3600)
    }
    register("long_swiftness") {
        translationKey("swiftness")
        addEffect(PotionEffectTypes.SPEED, 0, 9600)
    }
    register("strong_swiftness") {
        translationKey("swiftness")
        addEffect(PotionEffectTypes.SPEED, 1, 1800)
    }

    // Slowness
    register("slowness") {
        addEffect(PotionEffectTypes.SLOWNESS, 0, 1800)
    }
    register("long_slowness") {
        translationKey("slowness")
        addEffect(PotionEffectTypes.SLOWNESS, 0, 4800)
    }
    register("strong_slowness") {
        translationKey("slowness")
        addEffect(PotionEffectTypes.SLOWNESS, 3, 400)
    }

    // Water breathing
    register("water_breathing") {
        addEffect(PotionEffectTypes.WATER_BREATHING, 0, 3600)
    }
    register("long_water_breathing") {
        translationKey("water_breathing")
        addEffect(PotionEffectTypes.WATER_BREATHING, 0, 9600)
    }

    // Healing
    register("healing") {
        addEffect(PotionEffectTypes.INSTANT_HEALTH, 0, 1)
    }
    register("strong_healing") {
        translationKey("healing")
        addEffect(PotionEffectTypes.INSTANT_HEALTH, 1, 1)
    }

    // Harming
    register("harming") {
        addEffect(PotionEffectTypes.INSTANT_DAMAGE, 0, 1)
    }
    register("strong_harming") {
        translationKey("harming")
        addEffect(PotionEffectTypes.INSTANT_DAMAGE, 1, 1)
    }

    // Poison
    register("poison") {
        addEffect(PotionEffectTypes.POISON, 0, 900)
    }
    register("long_poison") {
        translationKey("poison")
        addEffect(PotionEffectTypes.POISON, 0, 1800)
    }
    register("strong_poison") {
        translationKey("poison")
        addEffect(PotionEffectTypes.POISON, 1, 432)
    }

    // Regeneration
    register("regeneration") {
        addEffect(PotionEffectTypes.REGENERATION, 0, 900)
    }
    register("long_regeneration") {
        translationKey("regeneration")
        addEffect(PotionEffectTypes.REGENERATION, 0, 1800)
    }
    register("strong_regeneration") {
        translationKey("regeneration")
        addEffect(PotionEffectTypes.REGENERATION, 1, 450)
    }

    // Strength
    register("strength") {
        addEffect(PotionEffectTypes.STRENGTH, 0, 3600)
    }
    register("long_strength") {
        translationKey("strength")
        addEffect(PotionEffectTypes.STRENGTH, 0, 9600)
    }
    register("strong_strength") {
        translationKey("strength")
        addEffect(PotionEffectTypes.STRENGTH, 1, 1800)
    }

    // Weakness
    register("weakness") {
        addEffect(PotionEffectTypes.WEAKNESS, 0, 1800)
    }
    register("long_weakness") {
        translationKey("weakness")
        addEffect(PotionEffectTypes.WEAKNESS, 0, 4800)
    }

    // Luck
    register("luck") {
        addEffect(PotionEffectTypes.LUCK, 0, 6000)
    }

    // Slow falling
    register("slow_falling") {
        addEffect(PotionEffectTypes.SLOW_FALLING, 0, 1800)
    }
    register("long_slow_falling") {
        translationKey("slow_falling")
        addEffect(PotionEffectTypes.SLOW_FALLING, 0, 4800)
    }

    // Turtle master
    register("turtle_master") {
        addEffect(PotionEffectTypes.SLOWNESS, 3, 400)
        addEffect(PotionEffectTypes.RESISTANCE, 2, 400)
    }
    register("long_turtle_master") {
        translationKey("turtle_master")
        addEffect(PotionEffectTypes.SLOWNESS, 3, 800)
        addEffect(PotionEffectTypes.RESISTANCE, 2, 800)
    }
    register("strong_turtle_master") {
        translationKey("turtle_master")
        addEffect(PotionEffectTypes.SLOWNESS, 5, 400)
        addEffect(PotionEffectTypes.RESISTANCE, 3, 400)
    }
}
