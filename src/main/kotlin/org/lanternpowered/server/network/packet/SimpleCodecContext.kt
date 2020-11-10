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
package org.lanternpowered.server.network.packet

import io.netty.channel.Channel
import org.lanternpowered.server.network.NetworkSession
import org.lanternpowered.server.network.buffer.ByteBufferAllocator
import org.lanternpowered.server.network.packet.CodecContext

class SimpleCodecContext(
        private val byteBufferAlloc: ByteBufferAllocator,
        override val channel: Channel,
        override val session: NetworkSession
) : CodecContext {

    override fun byteBufAlloc(): ByteBufferAllocator = this.byteBufferAlloc
}
