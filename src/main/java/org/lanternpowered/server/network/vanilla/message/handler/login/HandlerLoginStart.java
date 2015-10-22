/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.lanternpowered.server.game.LanternGameProfile;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInStart;
import org.lanternpowered.server.network.message.Async;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.handshake.MessageHandshakeIn.ProxyData;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInStart;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutEncryptionRequest;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutSuccess;
import org.lanternpowered.server.util.SecurityHelper;

@Async
public final class HandlerLoginStart implements Handler<MessageLoginInStart> {

    @Override
    public void handle(Session session, MessageLoginInStart message) {
        String username = message.getUsername();

        if (session.getServer().getOnlineMode()) {
            byte[] publicKey = SecurityHelper.generateX509Key(session.getServer().getKeyPair()
                    .getPublic()).getEncoded(); // Convert to X509 format
            byte[] verifyToken = SecurityHelper.generateVerifyToken();

            // Set verify data on session for use in the response handler
            session.setVerifyToken(verifyToken);
            session.setVerifyUsername(username);

            // Send created request message and wait for the response
            session.send(new MessageLoginOutEncryptionRequest(publicKey, verifyToken));
        } else {
            ProxyData proxy = session.getProxyData();
            LanternGameProfile profile;
            if (proxy == null) {
                UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
                profile = new LanternGameProfile(uuid, username);
            } else {
                profile = new LanternGameProfile(proxy.getUniqueId(), username, proxy.getProperties());
            }
            session.setPlayer(profile);
            session.send(new MessageLoginOutSuccess(profile.getUniqueId(), username));
            session.setProtocolState(ProtocolState.FORGE_HANDSHAKE);
            session.messageReceived(new MessageForgeHandshakeInStart());
        }
    }
}
