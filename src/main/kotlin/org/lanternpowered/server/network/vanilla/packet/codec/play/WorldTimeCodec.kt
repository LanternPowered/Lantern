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
import org.lanternpowered.server.network.packet.codec.Codec
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.WorldTimePacket
import org.lanternpowered.server.world.TimeUniverse

object WorldTimeCodec : Codec<WorldTimePacket> {

    private const val length = Long.SIZE_BYTES * 2

    override fun encode(context: CodecContext, packet: WorldTimePacket): ByteBuffer {
        val buf = context.byteBufAlloc().buffer(this.length)

        // The time also uses a negative tag
        var time = packet.time.toLong()
        while (time < 0)
            time += TimeUniverse.TICKS_IN_A_DAY.toLong()
        time %= TimeUniverse.TICKS_IN_A_DAY.toLong()
        time += packet.moonPhase.ordinal * TimeUniverse.TICKS_IN_A_DAY.toLong()
        if (!packet.enabled)
            time = if (time == 0L) -1 else -time

        buf.writeLong(packet.age)
        buf.writeLong(time)
        return buf
    }
}
