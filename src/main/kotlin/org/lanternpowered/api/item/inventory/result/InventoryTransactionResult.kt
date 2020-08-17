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
package org.lanternpowered.api.item.inventory.result

import org.lanternpowered.api.item.inventory.InventoryTransactionResultBuilder
import org.lanternpowered.api.item.inventory.ItemStackSnapshot

/**
 * Adds the provided items as stacks that have been rejected.
 */
fun InventoryTransactionResultBuilder.reject(vararg stacks: ItemStackSnapshot): InventoryTransactionResultBuilder =
        this.reject(stacks.asIterable())
