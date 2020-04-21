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
package org.lanternpowered.server.item.comparator

import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.server.inventory.LanternItemStack
import org.spongepowered.api.data.Key

class ItemDataComparator(vararg ignoredKeys: Key<*>) : Comparator<ItemStack> {

    private val ignoredKeys: Set<Key<*>> = ignoredKeys.toSet()

    override fun compare(o1: ItemStack, o2: ItemStack): Int {
        val registry1 = (o1 as LanternItemStack).keyRegistry
        val registry2 = (o2 as LanternItemStack).keyRegistry
        return 0 // TODO
    }
}
