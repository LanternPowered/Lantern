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
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes
import org.lanternpowered.server.network.packet.codec.CodecContext

class SmithingNetworkRecipe(
        id: String,
        type: String,
        private val result: ItemStack,
        private val base: NetworkIngredient,
        private val addition: NetworkIngredient
) : NetworkRecipe(id, type) {

    override fun write(ctx: CodecContext, buf: ByteBuffer) {
        this.base.write(ctx, buf)
        this.addition.write(ctx, buf)
        ctx.write(buf, ContextualValueTypes.ITEM_STACK, this.result)
    }
}
