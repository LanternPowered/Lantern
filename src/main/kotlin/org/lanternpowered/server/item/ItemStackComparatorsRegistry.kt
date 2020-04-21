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

import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackComparators
import java.util.function.Supplier

object ItemStackComparatorsRegistry : ItemStackComparators.Factory {

    override fun byData(): ItemStackComparators.Factory = apply {
        TODO("Not yet implemented")
    }

    override fun bySize(): ItemStackComparators.Factory = apply {
        TODO("Not yet implemented")
    }

    override fun asSupplier(): Supplier<Comparator<ItemStack>> {
        TODO("Not yet implemented")
    }

    override fun byType(): ItemStackComparators.Factory {
        TODO("Not yet implemented")
    }

    override fun byDurability(): ItemStackComparators.Factory {
        TODO("Not yet implemented")
    }

    override fun build(): Comparator<ItemStack> {
        TODO("Not yet implemented")
    }

}
