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
import org.lanternpowered.server.game.registry.InternalRegistries
import org.lanternpowered.server.game.registry.InternalRegistry

object EntityNetworkIDs {

    @JvmField
    val REGISTRY: InternalRegistry<NamespacedKey> = InternalRegistries.load("entity_type")
}
