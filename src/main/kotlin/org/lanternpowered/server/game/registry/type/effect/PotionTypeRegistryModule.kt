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
package org.lanternpowered.server.game.registry.type.effect

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.catalog.CatalogKeys.minecraft
import org.lanternpowered.api.effect.potion.PotionEffectTypes
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.item.potion.PotionType
import org.lanternpowered.api.item.potion.PotionTypeBuilder
import org.lanternpowered.api.item.potion.PotionTypes
import org.lanternpowered.server.game.registry.InternalPluginCatalogRegistryModule
import org.spongepowered.api.registry.util.RegistrationDependency

@RegistrationDependency(PotionEffectTypeRegistryModule::class)
object PotionTypeRegistryModule : InternalPluginCatalogRegistryModule<PotionType>(PotionTypes::class) {

    fun register(key: CatalogKey, fn: PotionTypeBuilder.() -> Unit = {}) {
        register(potionTypeOf(key, fn))
    }

    override fun registerDefaults() {
        register(minecraft("empty"))
        register(minecraft("water"))
        register(minecraft("mundane"))
        register(minecraft("thick"))
        register(minecraft("awkward"))

        // Night vision
        register(minecraft("night_vision")) {
            addEffect(PotionEffectTypes.NIGHT_VISION, 0, 3600)
        }
        register(minecraft("long_night_vision")) {
            translationKey("night_vision")
            addEffect(PotionEffectTypes.NIGHT_VISION, 0, 9600)
        }

        // Invisibility
        register(minecraft("invisibility")) {
            addEffect(PotionEffectTypes.INVISIBILITY, 0, 3600)
        }
        register(minecraft("long_invisibility")) {
            translationKey("invisibility")
            addEffect(PotionEffectTypes.INVISIBILITY, 0, 9600)
        }

        // Leaping
        register(minecraft("leaping")) {
            addEffect(PotionEffectTypes.JUMP_BOOST, 0, 3600)
        }
        register(minecraft("long_leaping")) {
            translationKey("leaping")
            addEffect(PotionEffectTypes.JUMP_BOOST, 0, 9600)
        }
        register(minecraft("strong_leaping")) {
            translationKey("leaping")
            addEffect(PotionEffectTypes.JUMP_BOOST, 1, 1800)
        }

        // Fire resistance
        register(minecraft("fire_resistance")) {
            addEffect(PotionEffectTypes.FIRE_RESISTANCE, 0, 3600)
        }
        register(minecraft("long_fire_resistance")) {
            translationKey("fire_resistance")
            addEffect(PotionEffectTypes.FIRE_RESISTANCE, 0, 9600)
        }

        // Swiftness
        register(minecraft("swiftness")) {
            addEffect(PotionEffectTypes.SPEED, 0, 3600)
        }
        register(minecraft("long_swiftness")) {
            translationKey("swiftness")
            addEffect(PotionEffectTypes.SPEED, 0, 9600)
        }
        register(minecraft("strong_swiftness")) {
            translationKey("swiftness")
            addEffect(PotionEffectTypes.SPEED, 1, 1800)
        }

        // Slowness
        register(minecraft("slowness")) {
            addEffect(PotionEffectTypes.SLOWNESS, 0, 1800)
        }
        register(minecraft("long_slowness")) {
            translationKey("slowness")
            addEffect(PotionEffectTypes.SLOWNESS, 0, 4800)
        }
        register(minecraft("strong_slowness")) {
            translationKey("slowness")
            addEffect(PotionEffectTypes.SLOWNESS, 3, 400)
        }

        // Water breathing
        register(minecraft("water_breathing")) {
            addEffect(PotionEffectTypes.WATER_BREATHING, 0, 3600)
        }
        register(minecraft("long_water_breathing")) {
            translationKey("water_breathing")
            addEffect(PotionEffectTypes.WATER_BREATHING, 0, 9600)
        }

        // Healing
        register(minecraft("healing")) {
            addEffect(PotionEffectTypes.INSTANT_HEALTH, 0, 1)
        }
        register(minecraft("strong_healing")) {
            translationKey("healing")
            addEffect(PotionEffectTypes.INSTANT_HEALTH, 1, 1)
        }

        // Harming
        register(minecraft("harming")) {
            addEffect(PotionEffectTypes.INSTANT_DAMAGE, 0, 1)
        }
        register(minecraft("strong_harming")) {
            translationKey("harming")
            addEffect(PotionEffectTypes.INSTANT_DAMAGE, 1, 1)
        }

        // Poison
        register(minecraft("poison")) {
            addEffect(PotionEffectTypes.POISON, 0, 900)
        }
        register(minecraft("long_poison")) {
            translationKey("poison")
            addEffect(PotionEffectTypes.POISON, 0, 1800)
        }
        register(minecraft("strong_poison")) {
            translationKey("poison")
            addEffect(PotionEffectTypes.POISON, 1, 432)
        }

        // Regeneration
        register(minecraft("regeneration")) {
            addEffect(PotionEffectTypes.REGENERATION, 0, 900)
        }
        register(minecraft("long_regeneration")) {
            translationKey("regeneration")
            addEffect(PotionEffectTypes.REGENERATION, 0, 1800)
        }
        register(minecraft("strong_regeneration")) {
            translationKey("regeneration")
            addEffect(PotionEffectTypes.REGENERATION, 1, 450)
        }

        // Strength
        register(minecraft("strength")) {
            addEffect(PotionEffectTypes.STRENGTH, 0, 3600)
        }
        register(minecraft("long_strength")) {
            translationKey("strength")
            addEffect(PotionEffectTypes.STRENGTH, 0, 9600)
        }
        register(minecraft("strong_strength")) {
            translationKey("strength")
            addEffect(PotionEffectTypes.STRENGTH, 1, 1800)
        }

        // Weakness
        register(minecraft("weakness")) {
            addEffect(PotionEffectTypes.WEAKNESS, 0, 1800)
        }
        register(minecraft("long_weakness")) {
            translationKey("weakness")
            addEffect(PotionEffectTypes.WEAKNESS, 0, 4800)
        }

        // Luck
        register(minecraft("luck")) {
            addEffect(PotionEffectTypes.LUCK, 0, 6000)
        }

        // Slow falling
        register(minecraft("slow_falling")) {
            addEffect(PotionEffectTypes.SLOW_FALLING, 0, 1800)
        }
        register(minecraft("long_slow_falling")) {
            translationKey("slow_falling")
            addEffect(PotionEffectTypes.SLOW_FALLING, 0, 4800)
        }

        // Turtle master
        register(minecraft("turtle_master")) {
            addEffect(PotionEffectTypes.SLOWNESS, 3, 400)
            addEffect(PotionEffectTypes.RESISTANCE, 2, 400)
        }
        register(minecraft("long_turtle_master")) {
            translationKey("turtle_master")
            addEffect(PotionEffectTypes.SLOWNESS, 3, 800)
            addEffect(PotionEffectTypes.RESISTANCE, 2, 800)
        }
        register(minecraft("strong_turtle_master")) {
            translationKey("turtle_master")
            addEffect(PotionEffectTypes.SLOWNESS, 5, 400)
            addEffect(PotionEffectTypes.RESISTANCE, 3, 400)
        }
    }
}
