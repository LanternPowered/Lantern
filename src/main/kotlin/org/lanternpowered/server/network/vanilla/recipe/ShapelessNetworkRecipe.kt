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
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.spongepowered.api.item.inventory.ItemStack

class ShapelessNetworkRecipe(
        id: String, group: String?,
        private val result: ItemStack,
        private val ingredients: List<NetworkIngredient>
) : GroupedNetworkRecipe(id, NetworkRecipeTypes.CRAFTING_SHAPELESS, group) {

    override fun write(ctx: CodecContext, buf: ByteBuffer) {
        super.write(ctx, buf)
        buf.writeVarInt(this.ingredients.size)
        this.ingredients.forEach { ingredient -> ingredient.write(ctx, buf) }
        ctx.write(buf, ContextualValueTypes.ITEM_STACK, result)
    }
}
