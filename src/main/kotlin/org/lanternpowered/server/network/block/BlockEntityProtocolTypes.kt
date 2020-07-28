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
package org.lanternpowered.server.network.block

import org.lanternpowered.api.registry.CatalogRegistry
import org.lanternpowered.api.registry.provide
import org.spongepowered.api.block.entity.BlockEntity

object BlockEntityProtocolTypes {

    val BANNER: BlockEntityProtocolType<BlockEntity> by CatalogRegistry.provide("BANNER")

    val DEFAULT: BlockEntityProtocolType<BlockEntity> by CatalogRegistry.provide("DEFAULT")

    val SIGN: BlockEntityProtocolType<BlockEntity> by CatalogRegistry.provide("SIGN")
}
