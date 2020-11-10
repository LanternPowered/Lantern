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

import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.server.entity.LanternEntity
import kotlin.reflect.KClass

interface EntityProtocolType<E : LanternEntity> : CatalogType {
    val entityType: KClass<E>
    val supplier: (E) -> AbstractEntityProtocol<E>
}
