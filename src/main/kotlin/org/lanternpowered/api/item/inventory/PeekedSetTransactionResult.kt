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
package org.lanternpowered.api.item.inventory

/**
 * The transaction result of a peeked set operation.
 */
interface PeekedSetTransactionResult : PeekedTransactionResult {

    /**
     * The item that was rejected. Or empty if the stack was set completely.
     */
    val rejectedItem: ItemStackSnapshot
}
