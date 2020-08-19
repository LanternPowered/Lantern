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

import org.lanternpowered.api.item.inventory.ExtendedBlockEntityInventory
import org.spongepowered.api.block.entity.BlockEntity
import org.spongepowered.api.item.inventory.Carrier
import java.util.Optional

interface AbstractBlockEntityInventory<C> : AbstractCarriedInventory<C>, ExtendedBlockEntityInventory<C>
        where C : Carrier,
              C : BlockEntity {

    override fun getBlockEntity(): Optional<C> = this.carrier

    override fun markDirty() {}
}
