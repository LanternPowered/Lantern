package org.lanternpowered.server.network.packet

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.codec.CodecContext

interface PacketEncoder<P : Packet> {

    /**
     * Encodes the packet into a byte buffer.
     *
     * @param context The context
     * @param packet The packet to encode
     * @return The encoded byte buffer
     */
    fun encode(context: CodecContext, packet: P): ByteBuffer
}
