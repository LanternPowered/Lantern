/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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

import com.google.common.collect.Sets;
import io.netty.util.Attribute;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.forge.handshake.ForgeHandshakePhase;
import org.lanternpowered.server.network.forge.handshake.ForgeServerHandshakePhase;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutHello;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInStart;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutRegisterChannels;
import org.spongepowered.api.Platform;

import java.util.Set;

public final class HandlerForgeHandshakeInStart implements Handler<MessageForgeHandshakeInStart> {

    @Override
    public void handle(NetworkContext context, MessageForgeHandshakeInStart message) {
        Attribute<ForgeServerHandshakePhase> phase = context.getChannel().attr(ForgeHandshakePhase.PHASE);
        Session session = context.getSession();
        if (phase.get() != null && phase.get() != ForgeServerHandshakePhase.START) {
            session.disconnect("Retrieved unexpected forge handshake start message.");
            return;
        }
        boolean fml = session.getChannel().attr(Session.FML_MARKER).get();

        Set<String> channels = Sets.newHashSet(LanternGame.get().getChannelRegistrar()
                .getRegisteredChannels(Platform.Type.SERVER));
        if (fml) {
            channels.add("FML");
            channels.add("FML|HS");
            channels.add("FML|MP");
        }
        if (!channels.isEmpty()) {
            session.send(new MessagePlayInOutRegisterChannels(channels));
        }
        if (fml) {
            phase.set(ForgeServerHandshakePhase.HELLO);
            session.send(new MessageForgeHandshakeInOutHello());
            LanternGame.log().info("{}: Start forge handshake.", session.getGameProfile().getName().get());
        } else {
            LanternGame.log().info("{}: Skip forge handshake.", session.getGameProfile().getName().get());
            phase.set(ForgeServerHandshakePhase.DONE);
            session.setProtocolState(ProtocolState.PLAY);
            session.spawnPlayer();
        }
    }
}
