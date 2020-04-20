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

import org.lanternpowered.api.Lantern

/**
 * The game registry.
 */
interface GameRegistry : org.spongepowered.api.registry.GameRegistry {

    override fun getBuilderRegistry(): BuilderRegistry
    override fun getCatalogRegistry(): CatalogRegistry
    override fun getFactoryRegistry(): FactoryRegistry
    override fun getRecipeRegistry(): RecipeRegistry
    override fun getVillagerRegistry(): VillagerRegistry

    /**
     * The singleton instance of the game registry.
     */
    companion object : GameRegistry by Lantern.registry
}
