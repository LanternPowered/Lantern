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

data class ClientRecipeBookStatePacket(
        val type: Type,
        val state: RecipeBookState
) : Packet {

    enum class Type {
        CRAFTING,
        FURNACE,
        BLAST_FURNACE,
        SMOKER
    }
}
