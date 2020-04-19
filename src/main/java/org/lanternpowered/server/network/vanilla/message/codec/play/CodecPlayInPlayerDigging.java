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
package org.lanternpowered.server.network.vanilla.message.codec.play;

import static org.lanternpowered.server.network.vanilla.message.codec.play.CodecUtils.decodeDirection;

import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.DecoderException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInDropHeldItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutFinishUsingItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerDigging;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInSwapHandItems;
import org.spongepowered.math.vector.Vector3i;

public final class CodecPlayInPlayerDigging implements Codec<Message> {

    @Override
    public Message decode(CodecContext context, ByteBuffer buf) throws CodecException {
        int action = buf.readByte();
        Vector3i position = buf.readPosition();
        int face = buf.readByte();
        switch (action) {
            case 0:
            case 1:
            case 2:
                return new MessagePlayInPlayerDigging(MessagePlayInPlayerDigging.Action.values()[action],
                        position, decodeDirection(face));
            case 3:
            case 4:
                return new MessagePlayInDropHeldItem(action == 3);
            case 5:
                return new MessagePlayInOutFinishUsingItem();
            case 6:
                return new MessagePlayInSwapHandItems();
            default:
                throw new DecoderException("Unknown player digging message action: " + action);
        }
    }
}
