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

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.data.type.ArmorMaterial
import org.spongepowered.api.item.recipe.crafting.Ingredient

class LanternArmorMaterial @JvmOverloads constructor(key: NamespacedKey, repairIngredient: (() -> Ingredient)? = null) :
        DefaultCatalogType(key), ArmorMaterial {

    private val ingredient by lazy { repairIngredient?.invoke() }

    override fun getRepairIngredient() = this.ingredient.optional()
}
