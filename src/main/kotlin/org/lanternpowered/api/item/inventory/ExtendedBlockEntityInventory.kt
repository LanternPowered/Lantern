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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.item.inventory

import org.spongepowered.api.block.entity.BlockEntity
import org.spongepowered.api.item.inventory.Carrier
import java.util.Optional
import kotlin.contracts.contract

typealias BlockEntityInventory<C> = org.spongepowered.api.item.inventory.type.BlockEntityInventory<C>

/**
 * Gets the normal block entity inventory as an extended block entity inventory.
 */
inline fun <C> BlockEntityInventory<C>.fix(): ExtendedBlockEntityInventory<C>
        where C : Carrier,
              C : BlockEntity {
    contract { returns() implies (this@fix is ExtendedBlockEntityInventory) }
    return this as ExtendedBlockEntityInventory
}

/**
 * An extended version of [BlockEntityInventory].
 */
interface ExtendedBlockEntityInventory<C> : BlockEntityInventory<C>, ExtendedSpongeCarriedInventory<C>
        where C : Carrier,
              C : BlockEntity {

    @Deprecated("Prefer to use carrierOrNull()")
    override fun getBlockEntity(): Optional<C>

    @Deprecated("Redundant, serialization will be tracked automatically.")
    override fun markDirty()
}
