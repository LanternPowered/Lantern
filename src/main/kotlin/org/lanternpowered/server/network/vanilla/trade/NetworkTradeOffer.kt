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
import org.lanternpowered.server.network.value.ContextualValueCodec
import org.lanternpowered.server.network.item.NetworkItemStack
import org.lanternpowered.server.network.packet.CodecContext

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

    companion object : ContextualValueCodec<NetworkTradeOffer> {

        override fun write(ctx: CodecContext, buf: ByteBuffer, value: NetworkTradeOffer) {
            NetworkItemStack.write(ctx, buf, value.firstInput)
            NetworkItemStack.write(ctx, buf, value.output)
            if (value.secondInput.isEmpty) {
                buf.writeBoolean(false)
            } else {
                buf.writeBoolean(true)
                NetworkItemStack.write(ctx, buf, value.secondInput)
            }
            buf.writeBoolean(value.disabled)
            buf.writeInt(value.uses)
            buf.writeInt(value.maxUses)
            buf.writeInt(value.experience)
            buf.writeInt(value.specialPrice)
            buf.writeFloat(value.priceMultiplier.toFloat())
        }

        override fun read(ctx: CodecContext, buf: ByteBuffer): NetworkTradeOffer = throw UnsupportedOperationException()
    }
}
