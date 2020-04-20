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
package org.lanternpowered.api.registry

/**
 * The recipe registry.
 */
interface RecipeRegistry : org.spongepowered.api.item.recipe.RecipeRegistry {

    /**
     * The singleton instance of the recipe registry.
     */
    companion object : RecipeRegistry by GameRegistry.recipeRegistry
}
