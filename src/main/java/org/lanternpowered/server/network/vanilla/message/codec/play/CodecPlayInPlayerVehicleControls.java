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

import static com.google.common.base.MoreObjects.firstNonNull;

import io.netty.handler.codec.CodecException;
import io.netty.util.AttributeKey;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.BulkMessage;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerMovementInput;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerSneak;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerVehicleJump;

import java.util.ArrayList;
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

        final List<Message> messages = new ArrayList<>();
        final boolean lastSneak = firstNonNull(context.getChannel().attr(SNEAKING).getAndSet(sneak), false);
        if (lastSneak != sneak) {
            messages.add(new MessagePlayInPlayerSneak(sneak));
        }

        final boolean lastJump = firstNonNull(context.getChannel().attr(JUMPING).getAndSet(jump), false);
        if (lastJump != jump && !firstNonNull(
                context.getChannel().attr(CodecPlayInPlayerAction.CANCEL_NEXT_JUMP_MESSAGE).getAndSet(false), false)) {
            messages.add(new MessagePlayInPlayerVehicleJump(jump, 0f));
        }

        // The mc client already applies the sneak speed, but we want to choose it
        if (sneak) {
            sideways /= 0.3f;
            forwards /= 0.3f;
        }

        messages.add(new MessagePlayInPlayerMovementInput(forwards, sideways));
        return messages.size() == 1 ? messages.get(0) : new BulkMessage(messages);
    }
}
