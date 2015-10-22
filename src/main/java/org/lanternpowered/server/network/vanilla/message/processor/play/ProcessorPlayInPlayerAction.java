/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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
package org.lanternpowered.server.network.vanilla.message.processor.play;

import io.netty.handler.codec.CodecException;
import io.netty.util.AttributeKey;

import java.util.List;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInLeaveBed;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerVehicleJump;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerSneak;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerSprint;
import org.lanternpowered.server.network.vanilla.message.type.play.internal.MessagePlayInPlayerAction;

public final class ProcessorPlayInPlayerAction implements Processor<MessagePlayInPlayerAction> {

    public static final AttributeKey<Boolean> CANCEL_NEXT_JUMP_MESSAGE = AttributeKey.valueOf("cancel-next-jump-message");

    @Override
    public void process(CodecContext context, MessagePlayInPlayerAction message, List<Message> output) throws CodecException {
        int action = message.getAction();

        // Sneaking states
        if (action == 0 || action == 1) {
            output.add(new MessagePlayInPlayerSneak(action == 0));
        // Sprinting states
        } else if (action == 3 || action == 4) {
            output.add(new MessagePlayInPlayerSprint(action == 3));
        // Leave bed button is pressed
        } else if (action == 2) {
            output.add(new MessagePlayInLeaveBed());
        // Horse jump power action
        } else if (action == 5) {
            output.add(new MessagePlayInPlayerVehicleJump(false, message.getValue() / 100f));
            // Make sure that the vehicle movement message doesn't add the jump message as well
            context.channel().attr(CANCEL_NEXT_JUMP_MESSAGE).set(true);
        }
    }

}
