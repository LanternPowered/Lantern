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
package org.lanternpowered.server.registry.type.network

import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.api.registry.CatalogTypeRegistryBuilder
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.network.block.BlockEntityProtocol
import org.lanternpowered.server.network.block.BlockEntityProtocolType
import org.lanternpowered.server.network.block.SimpleBlockEntityProtocol
import org.lanternpowered.server.network.block.blockEntityProtocolTypeOf
import org.lanternpowered.server.network.block.vanilla.BannerBlockEntityProtocol
import org.lanternpowered.server.network.block.vanilla.SignBlockEntityProtocol
import org.spongepowered.api.block.entity.BlockEntity

private inline fun <reified T : BlockEntity> CatalogTypeRegistryBuilder<BlockEntityProtocolType<*>>.register(
        id: String, noinline supplier: (T) -> BlockEntityProtocol<T>
) = register(blockEntityProtocolTypeOf(minecraftKey(id), supplier))

val BlockEntityProtocolTypeRegistry = catalogTypeRegistry<BlockEntityProtocolType<*>> {
    register<BlockEntity>("banner", ::BannerBlockEntityProtocol)
    register<BlockEntity>("default", ::SimpleBlockEntityProtocol)
    register<BlockEntity>("sign", ::SignBlockEntityProtocol)
}
