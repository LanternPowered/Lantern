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
package org.lanternpowered.server.network.forge.message.handler.handshake;

import io.netty.util.Attribute;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.forge.handshake.ForgeHandshakePhase;
import org.lanternpowered.server.network.forge.handshake.ForgeServerHandshakePhase;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutHello;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.session.Session;

public final class HandlerForgeHandshakeInHello implements Handler<MessageForgeHandshakeInOutHello> {

    @Override
    public void handle(NetworkContext context, MessageForgeHandshakeInOutHello message) {
        Session session = context.getSession();
        Attribute<ForgeServerHandshakePhase> phase = session.getChannel().attr(ForgeHandshakePhase.PHASE);
        if (phase.get() != ForgeServerHandshakePhase.HELLO) {
            session.disconnect("Retrieved unexpected forge handshake hello message.");
            return;
        }
        Lantern.getLogger().info("{}: Forge handshake -> Received hello message.", session.getGameProfile().getName());
    }
}
