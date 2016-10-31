/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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

import io.netty.handler.codec.CodecException;
import io.netty.util.AttributeKey;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.NullMessage;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInLeaveBed;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerSneak;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerSprint;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerVehicleJump;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInRequestHorseInventory;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInStartElytraFlying;

public final class CodecPlayInPlayerAction implements Codec<Message> {

    static final AttributeKey<Boolean> CANCEL_NEXT_JUMP_MESSAGE = AttributeKey.valueOf("cancel-next-jump-message");

    @Override
    public Message decode(CodecContext context, ByteBuffer buf) throws CodecException {
        // Normally should this be the entity id, but only the
        // client player will send this, so it won't be used
        buf.readVarInt();
        final int action = buf.readVarInt();
        final int value = buf.readVarInt();
        // Sneaking states
        if (action == 0 || action == 1) {
            return new MessagePlayInPlayerSneak(action == 0);
        // Sprinting states
        } else if (action == 3 || action == 4) {
            return new MessagePlayInPlayerSprint(action == 3);
        // Leave bed button is pressed
        } else if (action == 2) {
            return new MessagePlayInLeaveBed();
        // Horse jump power action
        } else if (action == 5) {
            // Make sure that the vehicle movement message doesn't add the jump message as well
            context.getChannel().attr(CANCEL_NEXT_JUMP_MESSAGE).set(true);
            return new MessagePlayInPlayerVehicleJump(false, ((float) value) / 100f);
        } else if (action == 6) {
            // Open inventory, there is another message that sends this and this one will
            // be removed in 1.9, so ignoring it.
            return NullMessage.INSTANCE;
        } else if (action == 7) {
            return new MessagePlayInRequestHorseInventory();
        } else if (action == 8) {
            return new MessagePlayInStartElytraFlying();
        }
        throw new CodecException("Unknown action type: " + action);
    }
}
