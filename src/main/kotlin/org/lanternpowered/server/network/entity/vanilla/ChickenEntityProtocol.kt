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
package org.lanternpowered.server.network.entity.vanilla

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.server.entity.LanternEntity

class ChickenEntityProtocol<E : LanternEntity>(entity: E) : AnimalEntityProtocol<E>(entity) {

    companion object {
        private val TYPE = minecraftKey("chicken")
    }

    override val mobType: NamespacedKey get() = TYPE
}
