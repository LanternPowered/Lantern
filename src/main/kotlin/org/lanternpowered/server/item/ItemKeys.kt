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
