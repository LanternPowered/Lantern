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

import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.item.NetworkItemStack
import org.lanternpowered.server.network.packet.codec.CodecContext

class SmeltingNetworkRecipe(
        id: String,
        type: String,
        group: String?,
        private val result: ItemStack,
        private val ingredient: NetworkIngredient,
        private val experience: Double,
        private val smeltingTime: Int
) : GroupedNetworkRecipe(id, type, group) {

    override fun writeProperties(ctx: CodecContext, buf: ByteBuffer) {
        super.writeProperties(ctx, buf)
        this.ingredient.write(ctx, buf)
        NetworkItemStack.write(ctx, buf, this.result)
        buf.writeFloat(this.experience.toFloat())
        buf.writeVarInt(this.smeltingTime)
    }
}
