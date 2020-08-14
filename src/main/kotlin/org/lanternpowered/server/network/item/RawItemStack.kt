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

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.minecraftKey
import org.spongepowered.api.data.persistence.DataView

data class RawItemStack(
        val type: NamespacedKey,
        val amount: Int,
        val dataView: DataView?
) {

    val isEmpty: Boolean
        get() = this.amount <= 0

    companion object {

        /**
         * An empty raw item stack.
         */
        val Empty = RawItemStack(minecraftKey("empty"), 0, null)
    }
}
