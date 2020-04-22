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
package org.lanternpowered.server.registry

import org.lanternpowered.api.registry.RecipeRegistry
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.recipe.Recipe
import org.spongepowered.api.item.recipe.RecipeType
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipe
import org.spongepowered.api.world.World
import java.util.Optional

object LanternRecipeRegistry : RecipeRegistry {

    override fun <T : Recipe> getAllOfType(type: RecipeType<T>): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun getById(id: CatalogKey): Optional<Recipe> {
        TODO("Not yet implemented")
    }

    override fun getAll(): Collection<Recipe> {
        TODO("Not yet implemented")
    }

    override fun findMatchingRecipe(inventory: Inventory, world: World<out World<*>>): Optional<Recipe> {
        TODO("Not yet implemented")
    }

    override fun <T : Recipe> findMatchingRecipe(type: RecipeType<T>, inventory: Inventory, world: World<out World<*>>): Optional<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Recipe> findByResult(type: RecipeType<T>, result: ItemStackSnapshot): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : SmeltingRecipe> findSmeltingRecipe(type: RecipeType<T>, ingredient: ItemStackSnapshot): Optional<T> {
        TODO("Not yet implemented")
    }
}
