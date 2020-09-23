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
package org.lanternpowered.server.network.vanilla.packet.type.play

import org.lanternpowered.server.item.recipe.RecipeBookState
import org.lanternpowered.server.network.packet.Packet

sealed class UnlockRecipesPacket : Packet {

    abstract val recipeBookStates: RecipeBookStates
    abstract val recipeIds: List<String>

    data class Remove(
            override val recipeBookStates: RecipeBookStates,
            override val recipeIds: List<String>
    ) : UnlockRecipesPacket()

    data class Init(
            override val recipeBookStates: RecipeBookStates,
            override val recipeIds: List<String>,
            val recipeIdsToBeDisplayed: List<String>
    ) : UnlockRecipesPacket()

    data class Add(
            override val recipeBookStates: RecipeBookStates,
            override val recipeIds: List<String>
    ) : UnlockRecipesPacket()
}

data class RecipeBookStates(
        val crafting: RecipeBookState,
        val smelting: RecipeBookState,
        val blastFurnace: RecipeBookState,
        val smoker: RecipeBookState
)
