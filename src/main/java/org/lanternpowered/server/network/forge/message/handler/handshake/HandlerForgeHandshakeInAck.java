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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.util.Attribute;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.forge.handshake.ForgeClientHandshakePhase;
import org.lanternpowered.server.network.forge.handshake.ForgeHandshakePhase;
import org.lanternpowered.server.network.forge.handshake.ForgeServerHandshakePhase;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutAck;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeOutRegistryData;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.session.Session;

import java.util.List;

public final class HandlerForgeHandshakeInAck implements Handler<MessageForgeHandshakeInOutAck> {

    @Override
    public void handle(NetworkContext context, MessageForgeHandshakeInOutAck message) {
        Session session = context.getSession();
        Attribute<ForgeServerHandshakePhase> phase = context.getChannel().attr(ForgeHandshakePhase.PHASE);
        switch (phase.get()) {
            case WAITING_ACK:
                if (!message.getPhase().equals(ForgeClientHandshakePhase.WAITING_SERVER_DATA)) {
                    session.disconnect("Retrieved unexpected forge handshake ack message. (Got " + message.getPhase() +
                            ", expected " + ForgeClientHandshakePhase.WAITING_SERVER_DATA + ")");
                } else {
                    List<MessageForgeHandshakeOutRegistryData.Entry> entries = Lists.newArrayList();
                    entries.add(new MessageForgeHandshakeOutRegistryData.Entry("fml:items", Maps.newHashMap(), Lists.newArrayList()));
                    entries.add(new MessageForgeHandshakeOutRegistryData.Entry("fml:blocks", Maps.newHashMap(), Lists.newArrayList()));
                    session.send(new MessageForgeHandshakeOutRegistryData(entries));
                    session.send(new MessageForgeHandshakeInOutAck(ForgeServerHandshakePhase.WAITING_ACK));
                    phase.set(ForgeServerHandshakePhase.COMPLETE);
                }
                LanternGame.log().info("{}: Forge handshake -> Received ack (waitingServerData) message.", session.getGameProfile().getName());
                break;
            case COMPLETE:
                if (!message.getPhase().equals(ForgeClientHandshakePhase.WAITING_SERVER_COMPLETE)) {
                    session.disconnect("Retrieved unexpected forge handshake ack message. (Got " + message.getPhase() +
                            ", expected " + ForgeClientHandshakePhase.WAITING_SERVER_COMPLETE + ")");
                } else {
                    session.send(new MessageForgeHandshakeInOutAck(ForgeServerHandshakePhase.COMPLETE));
                    phase.set(ForgeServerHandshakePhase.DONE);
                }
                LanternGame.log().info("{}: Forge handshake -> Received ack (waitingServerComplete) message.", session.getGameProfile().getName());
                break;
            case DONE:
                if (!message.getPhase().equals(ForgeClientHandshakePhase.PENDING_COMPLETE) &&
                        !message.getPhase().equals(ForgeClientHandshakePhase.COMPLETE)) {
                    session.disconnect("Retrieved unexpected forge handshake ack message. (Got " + message.getPhase() +
                            ", expected " + ForgeClientHandshakePhase.PENDING_COMPLETE + " or " +
                            ForgeClientHandshakePhase.COMPLETE + ")");
                } else {
                    if (message.getPhase().equals(ForgeClientHandshakePhase.PENDING_COMPLETE)) {
                        session.send(new MessageForgeHandshakeInOutAck(ForgeServerHandshakePhase.DONE));
                        LanternGame.log().info("{}: Forge handshake -> Received ack (pendingComplete) message.", session.getGameProfile().getName());
                    } else {
                        session.setProtocolState(ProtocolState.PLAY);
                        session.spawnPlayer();
                        LanternGame.log().info("{}: Forge handshake -> Received ack (complete) message.", session.getGameProfile().getName());
                    }
                }
                break;
            case ERROR:
                break;
            default:
                session.disconnect("Retrieved unexpected forge handshake ack message. (Got " +
                        message.getPhase() + ")");
        }
    }
}
