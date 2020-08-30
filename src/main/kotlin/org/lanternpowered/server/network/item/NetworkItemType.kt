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
package org.lanternpowered.server.network.item

import org.lanternpowered.api.item.ItemType

/**
 * Represents an entry of the [NetworkItemTypeRegistry].
 *
 * If [networkId] and [internalId] are equal, it means that this
 * item type is a vanilla item type.
 *
 * @property type The actual item type this type represents
 * @property networkId The int id that the client uses to represent this item type (the appearance)
 * @property internalId The int id that the server assigned to the item type, to make it unique
 * @property originalMaxStackSize The maximum stack size that is originally applicable to item on the client,
 *                        this is not the one returned by the item type.
 */
data class NetworkItemType(
        val type: ItemType,
        val networkId: Int,
        val internalId: Int,
        val originalMaxStackSize: Int
) {

    val isVanilla: Boolean
        get() = this.networkId == this.internalId
}
