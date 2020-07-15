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

import org.lanternpowered.api.registry.MutableCatalogTypeRegistry
import org.lanternpowered.api.registry.RecipeRegistry
import org.lanternpowered.api.registry.mutableCatalogTypeRegistry
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.api.world.World
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.recipe.Recipe
import org.spongepowered.api.item.recipe.RecipeType
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipe
import java.util.Optional

object LanternRecipeRegistry : RecipeRegistry, MutableCatalogTypeRegistry<Recipe> by mutableCatalogTypeRegistry() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : Recipe> getAllOfType(type: RecipeType<T>): Collection<T> =
            this.all.filter { recipe -> recipe.type == type } as Collection<T>

    override fun getByKey(key: ResourceKey): Optional<Recipe> = this[key].optional()
    override fun getAll(): Collection<Recipe> = this.all

    override fun findMatchingRecipe(inventory: Inventory, world: World): Optional<Recipe> {
        TODO("Not yet implemented")
    }

    override fun <T : Recipe> findMatchingRecipe(type: RecipeType<T>, inventory: Inventory, world: World): Optional<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Recipe> findByResult(type: RecipeType<T>, result: ItemStackSnapshot): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : SmeltingRecipe> findSmeltingRecipe(type: RecipeType<T>, ingredient: ItemStackSnapshot): Optional<T> {
        TODO("Not yet implemented")
    }
}
