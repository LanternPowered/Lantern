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
package org.lanternpowered.server.network.vanilla.trade

import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes
import org.lanternpowered.server.network.packet.codec.CodecContext

data class NetworkTradeOffer(
        private val firstInput: ItemStack,
        private val secondInput: ItemStack,
        private val output: ItemStack,
        private val disabled: Boolean,
        private val uses: Int,
        private val maxUses: Int,
        private val experience: Int,
        private val specialPrice: Int,
        private val priceMultiplier: Double
) {

    internal fun write(ctx: CodecContext, buf: ByteBuffer) {
        ctx.write(buf, ContextualValueTypes.ITEM_STACK, this.firstInput)
        ctx.write(buf, ContextualValueTypes.ITEM_STACK, this.output)
        if (this.secondInput.isEmpty) {
            buf.writeBoolean(false)
        } else {
            buf.writeBoolean(true)
            ctx.write(buf, ContextualValueTypes.ITEM_STACK, this.secondInput)
        }
        buf.writeBoolean(this.disabled)
        buf.writeInt(this.uses)
        buf.writeInt(this.maxUses)
        buf.writeInt(this.experience)
        buf.writeInt(this.specialPrice)
        buf.writeFloat(this.priceMultiplier.toFloat())
    }
}
