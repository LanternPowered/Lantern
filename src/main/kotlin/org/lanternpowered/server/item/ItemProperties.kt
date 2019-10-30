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
import org.lanternpowered.api.data.property.Property
import org.lanternpowered.api.ext.propertyOf
import org.lanternpowered.server.item.property.BowProjectile
import org.spongepowered.api.data.type.DyeColor
import org.spongepowered.api.data.type.WoodType

object ItemProperties {

    @JvmField
    val BOW_PROJECTILE_PROVIDER: Property<BowProjectile<*>>
            = propertyOf(minecraft("bow_projectile_provider"))

    @JvmField
    val IS_ALWAYS_CONSUMABLE: Property<Boolean>
            = propertyOf(minecraft("is_always_consumable"))

    @JvmField
    val USE_COOLDOWN: Property<Int>
            = propertyOf(minecraft("use_cooldown"))

    @JvmField
    val IS_DUAL_WIELDABLE: Property<Boolean>
            = propertyOf(minecraft("is_dual_wieldable"))

    @JvmField
    val HEALTH_RESTORATION: Property<Double>
            = propertyOf(minecraft("health_restoration"))

    @JvmField
    val MAXIMUM_USE_DURATION: Property<Int>
            = propertyOf(minecraft("maximum_use_duration"))

    @JvmField
    val MINIMUM_USE_DURATION: Property<Int>
            = propertyOf(minecraft("minimum_use_duration"))

    @JvmField
    val DYE_COLOR: Property<DyeColor>
            = propertyOf(minecraft("dye_color"))

    @JvmField
    val WOOD_TYPE: Property<WoodType>
            = propertyOf(minecraft("wood_type"))

}
