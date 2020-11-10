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
package org.lanternpowered.server.network.entity

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.entity.LanternEntity
import kotlin.reflect.KClass

fun <E : LanternEntity> entityProtocolTypeOf(
        key: NamespacedKey, entityType: KClass<E>, supplier: (E) -> AbstractEntityProtocol<E>
): EntityProtocolType<E> = LanternEntityProtocolType(key, entityType, supplier)

private class LanternEntityProtocolType<E : LanternEntity>(
        key: NamespacedKey,
        override val entityType: KClass<E>,
        override val supplier: (E) -> AbstractEntityProtocol<E>
) : DefaultCatalogType(key), EntityProtocolType<E> {

    override fun toStringHelper(): ToStringHelper =
            super.toStringHelper().add("entityType", this.entityType)
}
