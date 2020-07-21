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
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerUseItem;
import org.spongepowered.api.data.type.HandTypes;

public final class CodecPlayInPlayerUseItem implements Codec<PacketPlayInPlayerUseItem> {

    @Override
    public PacketPlayInPlayerUseItem decode(CodecContext context, ByteBuffer buf) throws CodecException {
        return new PacketPlayInPlayerUseItem(buf.readVarInt() == 0 ? HandTypes.MAIN_HAND : HandTypes.OFF_HAND);
    }
}
