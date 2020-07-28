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

import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.block.entity.BlockEntity
import kotlin.reflect.KClass

inline fun <reified T : BlockEntity> blockEntityProtocolTypeOf(
        key: NamespacedKey, noinline supplier: (T) -> BlockEntityProtocol<T>
): BlockEntityProtocolType<T> = blockEntityProtocolTypeOf(key, T::class, supplier)

fun <T : BlockEntity> blockEntityProtocolTypeOf(
        key: NamespacedKey, type: KClass<T>, supplier: (T) -> BlockEntityProtocol<T>
): BlockEntityProtocolType<T> = LanternBlockEntityProtocolType(key, type.java, supplier)

interface BlockEntityProtocolType<T : BlockEntity> : CatalogType {

    val blockEntityType: Class<T>

    val supplier: (T) -> BlockEntityProtocol<T>
}

private class LanternBlockEntityProtocolType<T : BlockEntity>(
        key: NamespacedKey,
        override val blockEntityType: Class<T>,
        override val supplier: (T) -> BlockEntityProtocol<T>
) : DefaultCatalogType(key), BlockEntityProtocolType<T> {

    override fun toStringHelper(): ToStringHelper {
        return super.toStringHelper()
                .add("blockEntityType", this.blockEntityType.name)
    }
}
