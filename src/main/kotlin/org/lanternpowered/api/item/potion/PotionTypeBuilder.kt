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
package org.lanternpowered.api.item.potion

import org.lanternpowered.api.effect.potion.PotionEffect
import org.lanternpowered.api.effect.potion.PotionEffectType
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.registry.CatalogBuilder
import java.util.function.Supplier

/**
 * A builder to construct [PotionType]s.
 */
interface PotionTypeBuilder : CatalogBuilder<PotionType, PotionTypeBuilder> {

    /**
     * Adds a [PotionEffect].
     *
     * @param potionEffect The potion effect to add
     */
    fun addEffect(potionEffect: PotionEffect): PotionTypeBuilder

    /**
     * Adds a potion effect.
     */
    fun addEffect(type: PotionEffectType, amplifier: Int, duration: Int, ambient: Boolean = false, particles: Boolean = true) =
            addEffect(potionEffectOf(type, amplifier, duration, ambient, particles))

    /**
     * Adds a potion effect.
     */
    fun addEffect(type: Supplier<out PotionEffectType>, amplifier: Int, duration: Int, ambient: Boolean = false, particles: Boolean = true) =
            addEffect(potionEffectOf(type, amplifier, duration, ambient, particles))

    /**
     * Sets the (partial) translation key that is used to provide
     * translations based on the built potion type.
     *
     * For example, setting `weakness` becomes
     * `item.minecraft.splash_potion.effect.weakness` when
     * used to translate a splash potion item.
     *
     * Defaults to the catalog key value.
     *
     * @param key The (partial) translation key
     * @return This builder, for chaining
     */
    fun translationKey(key: String): PotionTypeBuilder
}
