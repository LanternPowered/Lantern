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
package org.lanternpowered.server.registry.type.attribute

import org.lanternpowered.api.attribute.AttributeOperation
import org.lanternpowered.server.attribute.LanternAttributeOperation
import org.lanternpowered.server.attribute.attributeOperationFunction
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.spongepowered.api.ResourceKey

val AttributeOperationRegistry = internalCatalogTypeRegistry<AttributeOperation> {
    fun register(id: String, priority: Int, changeValueImmediately: Boolean = false,
                 function: (base: Double, modifier: Double, currentValue: Double) -> Double) =
            register(LanternAttributeOperation(ResourceKey.minecraft(id), priority, changeValueImmediately, attributeOperationFunction(function)))

    register("add_amount", 0, false) { _, modifier, _ -> modifier }
    register("multiply_base", 1, false) { base, modifier, current -> base * modifier - current }
    register("multiply", 2, false) { _, modifier, current -> current * modifier - current }
}
