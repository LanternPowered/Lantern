package org.lanternpowered.server.network.packet

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.codec.CodecContext

interface PacketDecoder<P : Packet> {

    /**
     * Decodes the packet from an encoded byte buffer.
     *
     * @param context The context
     * @param buf The byte buffer to decode
     * @return The decoded packet
     */
    fun decode(context: CodecContext, buf: ByteBuffer): P
}
