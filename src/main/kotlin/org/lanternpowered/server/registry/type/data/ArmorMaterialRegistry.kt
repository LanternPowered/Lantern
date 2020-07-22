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
package org.lanternpowered.server.registry.type.data

import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.data.type.LanternArmorMaterial
import org.spongepowered.api.data.type.ArmorMaterial
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.recipe.crafting.Ingredient

val ArmorMaterialRegistry = catalogTypeRegistry<ArmorMaterial> {
    fun register(id: String, repairIngredient: (() -> Ingredient)? = null) =
            register(LanternArmorMaterial(NamespacedKey.minecraft(id), repairIngredient))

    register("chain")
    register("diamond") { Ingredient.of(ItemTypes.DIAMOND.get()) }
    register("gold") { Ingredient.of(ItemTypes.GOLD_INGOT.get()) }
    register("iron") { Ingredient.of(ItemTypes.IRON_INGOT.get()) }
    register("leather") { Ingredient.of(ItemTypes.LEATHER.get()) }
}
