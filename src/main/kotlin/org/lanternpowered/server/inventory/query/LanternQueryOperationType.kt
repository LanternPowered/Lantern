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
package org.lanternpowered.server.inventory.query

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.item.inventory.query.QueryOperation
import org.spongepowered.api.item.inventory.query.QueryOperationType

class LanternQueryOperationType<T>(key: ResourceKey, protected val queryOperator: QueryOperator<T>) :
        DefaultCatalogType(key), QueryOperationType<T> {

    override fun of(arg: T): QueryOperation<T> = LanternQueryOperation(this, arg)
}
