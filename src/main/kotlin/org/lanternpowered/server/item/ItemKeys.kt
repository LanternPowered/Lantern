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
package org.lanternpowered.server.item

import org.lanternpowered.api.catalog.CatalogKeys.minecraft
import org.lanternpowered.api.data.valueKeyOf
import org.lanternpowered.server.item.property.BowProjectile
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.type.DyeColor
import org.spongepowered.api.data.type.WoodType
import org.spongepowered.api.data.value.Value

object ItemKeys {

    @JvmField
    val BOW_PROJECTILE_PROVIDER: Key<Value<BowProjectile<*>>>
            = valueKeyOf(minecraft("bow_projectile_provider"))

    @JvmField
    val IS_ALWAYS_CONSUMABLE: Key<Value<Boolean>>
            = valueKeyOf(minecraft("is_always_consumable"))

    @JvmField
    val USE_COOLDOWN: Key<Value<Int>>
            = valueKeyOf(minecraft("use_cooldown"))

    @JvmField
    val IS_DUAL_WIELDABLE: Key<Value<Boolean>>
            = valueKeyOf(minecraft("is_dual_wieldable"))

    @JvmField
    val HEALTH_RESTORATION: Key<Value<Double>>
            = valueKeyOf(minecraft("health_restoration"))

    @JvmField
    val MAXIMUM_USE_DURATION: Key<Value<Int>>
            = valueKeyOf(minecraft("maximum_use_duration"))

    @JvmField
    val MINIMUM_USE_DURATION: Key<Value<Int>>
            = valueKeyOf(minecraft("minimum_use_duration"))

    @JvmField
    val DYE_COLOR: Key<Value<DyeColor>>
            = valueKeyOf(minecraft("dye_color"))

    @JvmField
    val WOOD_TYPE: Key<Value<WoodType>>
            = valueKeyOf(minecraft("wood_type"))

}
