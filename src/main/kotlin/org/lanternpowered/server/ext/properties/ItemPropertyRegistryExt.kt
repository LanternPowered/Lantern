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
package org.lanternpowered.server.ext.properties

import org.lanternpowered.api.effect.potion.PotionEffect
import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.server.data.property.PropertyRegistry
import org.spongepowered.api.data.property.Properties
import org.spongepowered.api.data.type.ArmorType
import org.spongepowered.api.data.type.ToolType
import org.spongepowered.api.item.inventory.equipment.EquipmentType

fun <H : ItemType> PropertyRegistry<H>.applicablePotionEffects(vararg effects: PotionEffect) {
    register(Properties.APPLICABLE_POTION_EFFECTS, effects.toImmutableList())
}

fun <H : ItemType> PropertyRegistry<H>.applicablePotionEffects(effects: List<PotionEffect>) {
    register(Properties.APPLICABLE_POTION_EFFECTS, effects.toImmutableList())
}

fun <H : ItemType> PropertyRegistry<H>.applicablePotionEffects(fn: H.() -> List<PotionEffect>) {
    registerProvider(Properties.APPLICABLE_POTION_EFFECTS) {
        get(fn)
    }
}

fun <H : ItemType> PropertyRegistry<H>.replenishedFood(replenishedFood: Double) {
    register(Properties.REPLENISHED_FOOD, replenishedFood)
}

fun <H : ItemType> PropertyRegistry<H>.replenishedFood(fn: H.() -> Double) {
    registerProvider(Properties.REPLENISHED_FOOD) {
        get(fn)
    }
}

fun <H : ItemType> PropertyRegistry<H>.saturation(saturation: Double) {
    register(Properties.REPLENISHED_SATURATION, saturation)
}

fun <H : ItemType> PropertyRegistry<H>.saturation(fn: H.() -> Double) {
    registerProvider(Properties.REPLENISHED_SATURATION) {
        get(fn)
    }
}

fun <H : ItemType> PropertyRegistry<H>.healthRestoration(health: Double) {
    register(ItemProperties.HEALTH_RESTORATION, health)
}

fun <H : ItemType> PropertyRegistry<H>.healthRestoration(fn: H.() -> Double) {
    registerProvider(ItemProperties.HEALTH_RESTORATION) {
        get(fn)
    }
}

fun <H : ItemType> PropertyRegistry<H>.useLimit(limit: Int) {
    register(Properties.USE_LIMIT, limit)
}

fun <H : ItemType> PropertyRegistry<H>.useLimit(fn: H.() -> Int) {
    registerProvider(Properties.USE_LIMIT) {
        get(fn)
    }
}

fun <H : ItemType> PropertyRegistry<H>.useDuration(duration: Int) {
    register(ItemProperties.MINIMUM_USE_DURATION, duration)
    register(ItemProperties.MAXIMUM_USE_DURATION, duration)
}

fun <H : ItemType> PropertyRegistry<H>.useDuration(duration: IntRange) {
    register(ItemProperties.MINIMUM_USE_DURATION, duration.first)
    register(ItemProperties.MAXIMUM_USE_DURATION, duration.last)
}

fun <H : ItemType> PropertyRegistry<H>.cooldown(cooldown: Int) {
    register(ItemProperties.USE_COOLDOWN, cooldown)
}

fun <H : ItemType> PropertyRegistry<H>.cooldown(fn: H.() -> Int) {
    registerProvider(ItemProperties.USE_COOLDOWN) {
        get(fn)
    }
}

fun <H : ItemType> PropertyRegistry<H>.alwaysConsumable(alwaysConsumable: Boolean) {
    register(ItemProperties.IS_ALWAYS_CONSUMABLE, alwaysConsumable)
}

fun <H : ItemType> PropertyRegistry<H>.alwaysConsumable(fn: H.() -> Boolean) {
    registerProvider(ItemProperties.IS_ALWAYS_CONSUMABLE) {
        get(fn)
    }
}

fun <H : ItemType> PropertyRegistry<H>.dualWieldable(dualWield: Boolean) {
    register(ItemProperties.IS_DUAL_WIELDABLE, dualWield)
}

fun <H : ItemType> PropertyRegistry<H>.dualWieldable(fn: H.() -> Boolean) {
    registerProvider(ItemProperties.IS_DUAL_WIELDABLE) {
        get(fn)
    }
}

fun <H : ItemType> PropertyRegistry<H>.toolType(toolType: ToolType) {
    register(Properties.TOOL_TYPE, toolType)
}

fun <H : ItemType> PropertyRegistry<H>.toolType(fn: H.() -> ToolType) {
    registerProvider(Properties.TOOL_TYPE) {
        get(fn)
    }
}

fun <H : ItemType> PropertyRegistry<H>.armorType(armorType: ArmorType) {
    register(Properties.ARMOR_TYPE, armorType)
}

fun <H : ItemType> PropertyRegistry<H>.armorType(fn: H.() -> ArmorType) {
    registerProvider(Properties.ARMOR_TYPE) {
        get(fn)
    }
}

fun <H : ItemType> PropertyRegistry<H>.equipmentType(equipmentType: EquipmentType) {
    register(Properties.EQUIPMENT_TYPE, equipmentType)
}

fun <H : ItemType> PropertyRegistry<H>.equipmentType(fn: H.() -> EquipmentType) {
    registerProvider(Properties.EQUIPMENT_TYPE) {
        get(fn)
    }
}
