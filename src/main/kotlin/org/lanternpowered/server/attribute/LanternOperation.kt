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
package org.lanternpowered.server.attribute

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.util.annotation.CatalogedBy

@CatalogedBy(LanternOperations::class)
class LanternOperation(
        key: CatalogKey,
        private val priority: Int,
        private val changeValueImmediately: Boolean,
        private val function: OperationFunction
) : DefaultCatalogType(key), Comparable<LanternOperation> {

    override fun compareTo(other: LanternOperation): Int = this.priority - other.priority

    fun getIncrementation(base: Double, modifier: Double, currentValue: Double): Double {
        return this.function.getIncrementation(base, modifier, currentValue)
    }

    fun changeValueImmediately(): Boolean = this.changeValueImmediately
}
