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
package org.lanternpowered.server.network.vanilla.message.processor.play;

import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTheEnd;
import org.lanternpowered.server.network.vanilla.message.type.play.internal.MessagePlayOutChangeGameState;

import java.util.List;

public final class ProcessorPlayOutTheEnd implements Processor<MessagePlayOutTheEnd> {

    @Override
    public void process(CodecContext context, MessagePlayOutTheEnd message, List<Message> output) throws CodecException {
        output.add(new MessagePlayOutChangeGameState(4, message.shouldPlayCredits() ? 1 : 0));
    }
}
