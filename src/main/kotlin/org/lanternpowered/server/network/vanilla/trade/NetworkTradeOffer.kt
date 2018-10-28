/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.vanilla.trade

import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes
import org.lanternpowered.server.network.message.codec.CodecContext

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
        buf.writeInteger(this.uses)
        buf.writeInteger(this.maxUses)
        buf.writeInteger(this.experience)
        buf.writeInteger(this.specialPrice)
        buf.writeFloat(this.priceMultiplier.toFloat())
    }
}
