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
package org.lanternpowered.api.item.potion

import org.lanternpowered.api.effect.potion.PotionEffect
import org.lanternpowered.api.effect.potion.PotionEffectType
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.util.builder.CatalogBuilder

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
