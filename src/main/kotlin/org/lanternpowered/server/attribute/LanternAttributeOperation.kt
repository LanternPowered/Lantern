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

import org.lanternpowered.api.attribute.AttributeOperation
import org.lanternpowered.api.ResourceKey
import org.lanternpowered.server.catalog.DefaultCatalogType

class LanternAttributeOperation(
        key: ResourceKey,
        private val priority: Int,
        val changeValueImmediately: Boolean,
        private val function: AttributeOperationFunction
) : DefaultCatalogType(key), AttributeOperation, Comparable<LanternAttributeOperation> {

    override fun compareTo(other: LanternAttributeOperation): Int = this.priority - other.priority

    fun getIncrementation(base: Double, modifier: Double, currentValue: Double): Double =
            this.function.getIncrementation(base, modifier, currentValue)
}
