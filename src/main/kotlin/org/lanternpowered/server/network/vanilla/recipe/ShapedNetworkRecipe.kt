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
package org.lanternpowered.server.network.vanilla.recipe

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.item.NetworkItemStack
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.spongepowered.api.item.inventory.ItemStack

class ShapedNetworkRecipe(
        id: String,
        group: String?,
        private val result: ItemStack,
        private val ingredients: Array<Array<NetworkIngredient>>
) : GroupedNetworkRecipe(id, NetworkRecipeTypes.CRAFTING_SHAPED, group) {

    override fun writeProperties(ctx: CodecContext, buf: ByteBuffer) {
        val width = this.ingredients.size
        val height = this.ingredients[0].size
        buf.writeVarInt(width)
        buf.writeVarInt(height)
        super.writeProperties(ctx, buf)
        for (j in 0 until height) {
            for (i in 0 until width)
                this.ingredients[i][j].write(ctx, buf)
        }
        NetworkItemStack.write(ctx, buf, this.result)
    }
}
