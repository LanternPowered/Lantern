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
package org.lanternpowered.server.inventory

import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.item.inventory.ExtendedViewableInventory
import org.lanternpowered.api.util.collections.toImmutableSet
import org.spongepowered.api.item.inventory.menu.InventoryMenu

interface AbstractViewableInventory : ExtendedViewableInventory {

    override fun getViewers(): Set<Player> =
            (this as AbstractInventory).viewersSet.toImmutableSet()

    override fun hasViewers(): Boolean =
            (this as AbstractInventory).viewersSet.isNotEmpty()

    override fun canInteractWith(player: Player): Boolean = true

    override fun asMenu(): InventoryMenu {
        TODO("Not yet implemented")
    }
}
