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
package org.lanternpowered.server.network.vanilla.message.handler.login;

import static org.lanternpowered.server.network.session.Session.COMPRESSION;

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInStart;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.pipeline.MessageCompressionHandler;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInFinish;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutSetCompression;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutSuccess;
import org.lanternpowered.server.profile.LanternGameProfile;

public final class HandlerLoginFinish implements Handler<MessageLoginInFinish> {

    @Override
    public void handle(NetworkContext context, MessageLoginInFinish message) {
        final LanternGameProfile gameProfile = message.getGameProfile();
        final Session session = context.getSession();
        int compressionThreshold = Lantern.getGame().getGlobalConfig().getNetworkCompressionThreshold();
        if (compressionThreshold != -1) {
            session.send(new MessageLoginOutSetCompression(compressionThreshold)).addListener(future ->
                    context.getChannel().pipeline().replace(COMPRESSION, COMPRESSION, new MessageCompressionHandler(compressionThreshold)));
        }
        Lantern.getGame().getGameProfileManager().getCache().add(gameProfile, true, null);
        session.send(new MessageLoginOutSuccess(gameProfile.getUniqueId(), gameProfile.getName().get()))
                .addListener(future -> {
                    session.setProfile(gameProfile);
                    session.setProtocolState(ProtocolState.FORGE_HANDSHAKE);
                    session.messageReceived(new MessageForgeHandshakeInStart());
                });
    }
}
