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
package org.lanternpowered.server.item.behavior.vanilla.consumable

import org.lanternpowered.api.ext.immutableListOf
import org.lanternpowered.api.ext.merge
import org.lanternpowered.api.ext.orNull
import org.lanternpowered.api.ext.toImmutableSet
import org.spongepowered.api.data.Keys
import org.spongepowered.api.effect.potion.PotionEffect
import org.spongepowered.api.item.inventory.ItemStack

val PotionEffectsProvider: ItemStack.() -> Collection<PotionEffect> = {
    val potionType = get(Keys.POTION_TYPE).orNull()
    // The base potion effects based on the potion type
    var potionEffects = potionType?.effects
    // Add extra customizable potion effects
    val extraPotionEffects = get(Keys.POTION_EFFECTS).orNull()
    if (extraPotionEffects != null) {
        potionEffects = potionEffects?.merge(extraPotionEffects) ?: extraPotionEffects
    }
    potionEffects?.toImmutableSet() ?: immutableListOf()
}
