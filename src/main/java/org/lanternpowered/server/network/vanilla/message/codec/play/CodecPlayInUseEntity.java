/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.vanilla.message.codec.play;

import com.flowpowered.math.vector.Vector3d;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.DecoderException;
import org.lanternpowered.server.entity.living.player.PlayerHand;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInUseEntity;

public final class CodecPlayInUseEntity implements Codec<MessagePlayInUseEntity> {

    @Override
    public MessagePlayInUseEntity decode(CodecContext context, ByteBuf buf) throws CodecException {
        int entityId = context.readVarInt(buf);
        int action = context.readVarInt(buf);
        if (action == 1) {
            return new MessagePlayInUseEntity.Attack(entityId);
        } else if (action == 0 || action == 2) {
            Vector3d position = null;
            if (action == 2) {
                double x = buf.readFloat();
                double y = buf.readFloat();
                double z = buf.readFloat();
                position = new Vector3d(x, y, z);
            }
            PlayerHand hand = PlayerHand.values()[context.readVarInt(buf)];
            return new MessagePlayInUseEntity.Interact(entityId, hand, position);
        } else {
            throw new DecoderException("Recieved a UseEntity message with a unknown action: " + action);
        }
    }
}
