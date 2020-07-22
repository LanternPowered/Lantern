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
package org.lanternpowered.server.data.type

import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.data.type.ToolType
import org.spongepowered.api.item.recipe.crafting.Ingredient

class LanternToolType(key: NamespacedKey) : DefaultCatalogType(key), ToolType {
    override fun getRepairIngredient(): Ingredient {
        TODO("Not yet implemented")
    }
}
