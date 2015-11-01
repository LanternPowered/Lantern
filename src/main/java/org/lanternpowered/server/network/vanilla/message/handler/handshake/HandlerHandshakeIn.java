/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.network.vanilla.message.handler.handshake;

import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.game.LanternMinecraftVersion;
import org.lanternpowered.server.network.message.Async;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.handshake.MessageHandshakeIn;

@Async
public final class HandlerHandshakeIn implements Handler<MessageHandshakeIn> {

    @Override
    public void handle(Session session, MessageHandshakeIn message) {
        ProtocolState next = ProtocolState.fromId(message.getNextState());
        if (next == null) {
            session.disconnect("Unknown protocol state! (" + message.getNextState() + ")");
        }

        session.setProtocolState(next);
        if (!next.equals(ProtocolState.LOGIN) && !next.equals(ProtocolState.STATUS)) {
            session.disconnect("Received a unexpected handshake message! (" + next + ")");
        }
        // session.setVirtualHost(message.getAddress());
        session.setProtocolVersion(message.getProtocolVersion());
        session.getChannel().attr(Session.FML_MARKER).set(message.hasFMLMarker());

        if (next == ProtocolState.LOGIN) {
            int protocol = ((LanternMinecraftVersion) LanternGame.get().getPlatform().getMinecraftVersion()).getProtocol();

            if (message.getProtocolVersion() < protocol) {
                session.disconnect("Outdated client! I'm running " + LanternGame.get().getPlatform().getMinecraftVersion().getName());
            } else if (message.getProtocolVersion() > protocol) {
                session.disconnect("Outdated server! I'm running " + LanternGame.get().getPlatform().getMinecraftVersion().getName());
            }
        }
    }
}
