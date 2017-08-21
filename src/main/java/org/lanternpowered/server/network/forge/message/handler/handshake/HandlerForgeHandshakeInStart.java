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
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.NetworkSession;
import org.lanternpowered.server.network.forge.ForgeProtocol;
import org.lanternpowered.server.network.forge.handshake.ForgeServerHandshakePhase;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutHello;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInStart;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutRegisterChannels;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;

import java.util.HashSet;
import java.util.Set;

public final class HandlerForgeHandshakeInStart implements Handler<MessageForgeHandshakeInStart> {

    @Override
    public void handle(NetworkContext context, MessageForgeHandshakeInStart message) {
        final Attribute<ForgeServerHandshakePhase> phase = context.getChannel().attr(ForgeProtocol.HANDSHAKE_PHASE);
        final NetworkSession session = context.getSession();
        if (phase.get() != null && phase.get() != ForgeServerHandshakePhase.START) {
            session.disconnect(t("Retrieved unexpected forge handshake start message."));
            return;
        }
        final boolean fml = session.getChannel().attr(NetworkSession.FML_MARKER).get();

        final Set<String> channels = new HashSet<>(Sponge.getChannelRegistrar()
                .getRegisteredChannels(Platform.Type.SERVER));
        if (fml) {
            channels.add(ForgeProtocol.MAIN_CHANNEL);
            channels.add(ForgeProtocol.HANDSHAKE_CHANNEL);
            channels.add(ForgeProtocol.MULTI_PART_MESSAGE_CHANNEL);
        }
        if (!channels.isEmpty()) {
            session.send(new MessagePlayInOutRegisterChannels(channels));
        }
        // Disable Forge for now, we need to send the registries and stuff,
        // which isn't actually used. We may also remove the protocol in the
        // future if sponge uses completely it's own protocol.
        if (Lantern.getGame().getGlobalConfig().getForge().isEnabled() && fml) {
            phase.set(ForgeServerHandshakePhase.HELLO);
            session.send(new MessageForgeHandshakeInOutHello());
            Lantern.getLogger().debug("{}: Start forge handshake.", session.getGameProfile().getName().get());
        } else {
            Lantern.getLogger().debug("{}: Skip forge handshake.", session.getGameProfile().getName().get());
            phase.set(ForgeServerHandshakePhase.DONE);
            session.setProtocolState(ProtocolState.PLAY);
            session.initPlayer();
        }
    }
}
