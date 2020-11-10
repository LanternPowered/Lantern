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
package org.lanternpowered.api.attribute

import org.lanternpowered.api.registry.CatalogRegistry
import org.lanternpowered.api.registry.provide

object AttributeOperations {
    val ADD_AMOUNT: AttributeOperation by CatalogRegistry.provide("ADD_AMOUNT")
    val MULTIPLY: AttributeOperation by CatalogRegistry.provide("MULTIPLY")
    val MULTIPLY_BASE: AttributeOperation by CatalogRegistry.provide("MULTIPLY_BASE")
}
