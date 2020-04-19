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

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.ext.*
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.data.type.ArmorType
import org.spongepowered.api.item.recipe.crafting.Ingredient

class LanternArmorType @JvmOverloads constructor(key: CatalogKey, repairIngredient: () -> Ingredient? = { null }) :
        DefaultCatalogType(key), ArmorType {

    private val ingredient by lazy(repairIngredient)

    override fun getRepairIngredient() = this.ingredient.optional()
}
