/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import io.netty.handler.codec.CodecException;
import io.netty.util.AttributeKey;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.BulkMessage;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerSneak;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerVehicleJump;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerVehicleMovement;

import java.util.List;

public final class CodecPlayInPlayerVehicleControls implements Codec<Message> {

    private final static AttributeKey<Boolean> SNEAKING = AttributeKey.valueOf("last-sneaking-state");
    private final static AttributeKey<Boolean> JUMPING = AttributeKey.valueOf("last-jumping-state");

    @Override
    public Message decode(CodecContext context, ByteBuffer buf) throws CodecException {
        float sideways = buf.readFloat();
        float forwards = buf.readFloat();

        final byte flags = buf.readByte();

        final boolean jump = (flags & 0x1) != 0;
        final boolean sneak = (flags & 0x2) != 0;

        final List<Message> messages = Lists.newArrayList();
        final boolean lastSneak = MoreObjects.firstNonNull(context.getChannel().attr(SNEAKING).getAndSet(sneak), false);
        if (lastSneak != sneak) {
            messages.add(new MessagePlayInPlayerSneak(sneak));
        }

        final boolean lastJump = MoreObjects.firstNonNull(context.getChannel().attr(JUMPING).getAndSet(jump), false);
        if (lastJump != jump && !MoreObjects.firstNonNull(
                context.getChannel().attr(CodecPlayInPlayerAction.CANCEL_NEXT_JUMP_MESSAGE).getAndSet(false), false)) {
            messages.add(new MessagePlayInPlayerVehicleJump(jump, 0f));
        }

        // The mc client already applies the sneak speed, but we want to choose it
        if (sneak) {
            sideways /= 0.3f;
            forwards /= 0.3f;
        }

        messages.add(new MessagePlayInPlayerVehicleMovement(forwards, sideways));
        return messages.size() == 1 ? messages.get(0) : new BulkMessage(messages);
    }
}
