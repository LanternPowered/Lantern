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
package org.lanternpowered.server.network.forge.message.handler.handshake;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import io.netty.util.Attribute;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.forge.ForgeCatalogRegistryModule;
import org.lanternpowered.server.game.registry.forge.ForgeRegistryData;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.NetworkSession;
import org.lanternpowered.server.network.forge.ForgeProtocol;
import org.lanternpowered.server.network.forge.handshake.ForgeClientHandshakePhase;
import org.lanternpowered.server.network.forge.handshake.ForgeServerHandshakePhase;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutAck;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeOutRegistryData;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.protocol.ProtocolState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public final class HandlerForgeHandshakeInAck implements Handler<MessageForgeHandshakeInOutAck> {

    @Override
    public void handle(NetworkContext context, MessageForgeHandshakeInOutAck message) {
        final NetworkSession session = context.getSession();
        final Attribute<ForgeServerHandshakePhase> phase = context.getChannel().attr(ForgeProtocol.HANDSHAKE_PHASE);
        switch (phase.get()) {
            case WAITING_ACK:
                if (!message.getPhase().equals(ForgeClientHandshakePhase.WAITING_SERVER_DATA)) {
                    session.disconnect(t("Retrieved unexpected forge handshake ack message. (Got %s, expected %s)",
                            message.getPhase(), ForgeClientHandshakePhase.WAITING_SERVER_DATA));
                } else {
                    final List<MessageForgeHandshakeOutRegistryData.Entry> entries = new ArrayList<>();
                    Lantern.getRegistry().getCatalogRegistryModules().forEach(module -> {
                        if (module instanceof ForgeCatalogRegistryModule) {
                            final ForgeRegistryData data = ((ForgeCatalogRegistryModule) module).getRegistryData();
                            entries.add(new MessageForgeHandshakeOutRegistryData.Entry(
                                    data.getModuleId(), data.getMappings(), new HashMap<>(), new HashSet<>()));
                        }
                    });
                    session.send(new MessageForgeHandshakeOutRegistryData(entries));
                    session.send(new MessageForgeHandshakeInOutAck(ForgeServerHandshakePhase.WAITING_ACK));
                    phase.set(ForgeServerHandshakePhase.COMPLETE);
                }
                Lantern.getLogger().debug("{}: Forge handshake -> Received ack (waitingServerData) message.",
                        session.getGameProfile().getName().get());
                break;
            case COMPLETE:
                if (!message.getPhase().equals(ForgeClientHandshakePhase.WAITING_SERVER_COMPLETE)) {
                    session.disconnect(t("Retrieved unexpected forge handshake ack message. (Got %s, expected %s)",
                            message.getPhase(), ForgeClientHandshakePhase.WAITING_SERVER_COMPLETE));
                } else {
                    session.send(new MessageForgeHandshakeInOutAck(ForgeServerHandshakePhase.COMPLETE));
                    phase.set(ForgeServerHandshakePhase.DONE);
                }
                Lantern.getLogger().debug("{}: Forge handshake -> Received ack (waitingServerComplete) message.",
                        session.getGameProfile().getName().get());
                break;
            case DONE:
                if (!message.getPhase().equals(ForgeClientHandshakePhase.PENDING_COMPLETE) &&
                        !message.getPhase().equals(ForgeClientHandshakePhase.COMPLETE)) {
                    session.disconnect(t("Retrieved unexpected forge handshake ack message. (Got %s, expected %s or %s)",
                            message.getPhase(), ForgeClientHandshakePhase.PENDING_COMPLETE, ForgeClientHandshakePhase.COMPLETE));
                } else {
                    if (message.getPhase().equals(ForgeClientHandshakePhase.PENDING_COMPLETE)) {
                        session.send(new MessageForgeHandshakeInOutAck(ForgeServerHandshakePhase.DONE));
                        Lantern.getLogger().debug("{}: Forge handshake -> Received ack (pendingComplete) message.",
                                session.getGameProfile().getName().get());
                    } else {
                        session.setProtocolState(ProtocolState.PLAY);
                        session.initPlayer();
                        Lantern.getLogger().debug("{}: Forge handshake -> Received ack (complete) message.",
                                session.getGameProfile().getName().get());
                    }
                }
                break;
            case ERROR:
                break;
            default:
                session.disconnect(t("Retrieved unexpected forge handshake ack message. (Got %s)", message.getPhase()));
        }
    }
}
