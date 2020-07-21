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
package org.lanternpowered.server.network.vanilla.packet.codec.play;

import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.packet.codec.Codec;
import org.lanternpowered.server.network.packet.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutWorldTime;
import org.lanternpowered.server.world.TimeUniverse;

public final class CodecPlayOutWorldTime implements Codec<PacketPlayOutWorldTime> {

    private final static int LENGTH = Long.BYTES * 2;

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutWorldTime packet) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer(LENGTH);

        // The time also uses a negative tag
        long time = packet.getTime();
        while (time < 0) {
            time += TimeUniverse.TICKS_IN_A_DAY;
        }
        time %= TimeUniverse.TICKS_IN_A_DAY;
        time += packet.getMoonPhase().ordinal() * TimeUniverse.TICKS_IN_A_DAY;
        if (!packet.getEnabled()) {
            time = time == 0 ? -1 : -time;
        }

        buf.writeLong(packet.getAge());
        buf.writeLong(time);

        return buf;
    }
}
