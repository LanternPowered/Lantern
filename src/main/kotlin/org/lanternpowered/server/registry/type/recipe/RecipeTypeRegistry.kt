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
package org.lanternpowered.server.registry.type.recipe

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.item.recipe.Recipe
import org.spongepowered.api.item.recipe.RecipeType
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe
import org.spongepowered.api.item.recipe.single.StoneCutterRecipe
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipe

val RecipeTypeRegistry = catalogTypeRegistry<RecipeType<*>> {
    fun <T : Recipe> register(id: String) =
            register(LanternRecipeType<T>(CatalogKey.minecraft(id)))

    register<CraftingRecipe>("crafting")
    register<SmeltingRecipe>("smelting")
    register<SmeltingRecipe>("blasting")
    register<SmeltingRecipe>("smoking")
    register<SmeltingRecipe>("campfire_cooking")
    register<StoneCutterRecipe>("stonecutting")
}

private class LanternRecipeType<T : Recipe>(key: CatalogKey) : DefaultCatalogType(key), RecipeType<T>
