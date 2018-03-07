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
import org.lanternpowered.server.network.vanilla.message.codec.connection.CodecOutDisconnect;
import org.lanternpowered.server.network.vanilla.message.codec.login.CodecLoginInEncryptionResponse;
import org.lanternpowered.server.network.vanilla.message.codec.login.CodecLoginInStart;
import org.lanternpowered.server.network.vanilla.message.codec.login.CodecLoginOutEncryptionRequest;
import org.lanternpowered.server.network.vanilla.message.codec.login.CodecLoginOutSetCompression;
import org.lanternpowered.server.network.vanilla.message.codec.login.CodecLoginOutSuccess;
import org.lanternpowered.server.network.vanilla.message.handler.LoginProtocolHandler;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageOutDisconnect;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInEncryptionResponse;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInStart;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutEncryptionRequest;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutSetCompression;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutSuccess;

final class ProtocolLogin extends ProtocolBase {

    ProtocolLogin() {
        ////////////////////////
        /// Inbound Messages ///
        ////////////////////////

        final MessageRegistry inbound = inbound();

        inbound.bind(CodecLoginInStart.class,
                MessageLoginInStart.class);
        inbound.bind(CodecLoginInEncryptionResponse.class,
                MessageLoginInEncryptionResponse.class);

        inbound.addHandlerProvider((session, binder) -> binder.bind(new LoginProtocolHandler()));

        /////////////////////////
        /// Outbound Messages ///
        /////////////////////////

        final MessageRegistry outbound = outbound();

        outbound.bind(CodecOutDisconnect.class,
                MessageOutDisconnect.class);
        outbound.bind(CodecLoginOutEncryptionRequest.class,
                MessageLoginOutEncryptionRequest.class);
        outbound.bind(CodecLoginOutSuccess.class,
                MessageLoginOutSuccess.class);
        outbound.bind(CodecLoginOutSetCompression.class,
                MessageLoginOutSetCompression.class);
    }
}
