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

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.value.ContextualValueCodec
import org.lanternpowered.server.network.packet.codec.CodecContext

class NetworkTradeOfferContextualValueType : ContextualValueCodec<NetworkTradeOffer> {

    override fun write(ctx: CodecContext, buf: ByteBuffer, tradeOffer: NetworkTradeOffer) = tradeOffer.write(ctx, buf)
    override fun read(ctx: CodecContext, buf: ByteBuffer): NetworkTradeOffer = throw UnsupportedOperationException()
}
