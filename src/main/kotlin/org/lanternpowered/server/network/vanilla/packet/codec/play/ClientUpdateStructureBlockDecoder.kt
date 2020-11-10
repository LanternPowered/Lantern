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
package org.lanternpowered.server.network.vanilla.packet.codec.play

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.PacketDecoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientUpdateStructureBlockPacket
import org.spongepowered.math.vector.Vector3i

object ClientUpdateStructureBlockDecoder : PacketDecoder<ClientUpdateStructureBlockPacket> {

    private val modes = ClientUpdateStructureBlockPacket.Mode.values()
    private val rotations = ClientUpdateStructureBlockPacket.Rotation.values()
    private val mirrors = ClientUpdateStructureBlockPacket.Mirror.values()
    private val specialActions = ClientUpdateStructureBlockPacket.SpecialAction.values()

    override fun decode(ctx: CodecContext, buf: ByteBuffer): ClientUpdateStructureBlockPacket {
        val position = buf.readBlockPosition()
        val specialAction = this.specialActions[buf.readVarInt()]
        val mode = this.modes[buf.readVarInt()]
        val name = buf.readString()
        val offset = buf.readByteVector3i()
        val size = buf.readByteVector3i()
        val mirror = this.mirrors[buf.readVarInt()]
        val rotation = this.rotations[buf.readVarInt()]
        val metadata = buf.readString()
        val integrity = buf.readFloat().toDouble()
        val seed = buf.readVarLong()
        val flags = buf.readByte().toInt()
        val ignoreEntities = flags and 0x1 != 0
        val showAir = flags and 0x2 != 0
        val showBoundingBox = flags and 0x4 != 0
        return ClientUpdateStructureBlockPacket(position, specialAction, mode, name, offset, size, mirror, rotation,
                metadata, integrity, seed, ignoreEntities, showAir, showBoundingBox)
    }

    private fun ByteBuffer.readByteVector3i(): Vector3i {
        val x = this.readByte().toInt()
        val y = this.readByte().toInt()
        val z = this.readByte().toInt()
        return Vector3i(x, y, z)
    }
}
