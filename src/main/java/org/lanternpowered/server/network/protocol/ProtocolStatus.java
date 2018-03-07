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
package org.lanternpowered.server.network.protocol;

import org.lanternpowered.server.network.message.MessageRegistry;
import org.lanternpowered.server.network.vanilla.message.codec.status.CodecStatusInOutPing;
import org.lanternpowered.server.network.vanilla.message.codec.status.CodecStatusInRequest;
import org.lanternpowered.server.network.vanilla.message.codec.status.CodecStatusOutResponse;
import org.lanternpowered.server.network.vanilla.message.handler.StatusProtocolHandler;
import org.lanternpowered.server.network.vanilla.message.type.status.MessageStatusInOutPing;
import org.lanternpowered.server.network.vanilla.message.type.status.MessageStatusInRequest;
import org.lanternpowered.server.network.vanilla.message.type.status.MessageStatusOutResponse;

final class ProtocolStatus extends ProtocolBase {

    ProtocolStatus() {
        ////////////////////////
        /// Inbound Messages ///
        ////////////////////////

        final MessageRegistry inbound = inbound();

        inbound.bind(CodecStatusInRequest.class, MessageStatusInRequest.class);
        inbound.bind(CodecStatusInOutPing.class, MessageStatusInOutPing.class);

        inbound.addHandlerProvider((session, binder) -> binder.bind(new StatusProtocolHandler()));

        /////////////////////////
        /// Outbound Messages ///
        /////////////////////////

        final MessageRegistry outbound = outbound();

        outbound.bind(CodecStatusOutResponse.class, MessageStatusOutResponse.class);
        outbound.bind(CodecStatusInOutPing.class, MessageStatusInOutPing.class);
    }
}
