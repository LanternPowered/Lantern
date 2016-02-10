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

import io.netty.util.AttributeKey;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.Async;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.handshake.MessageHandshakeIn.ProxyData;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInFinish;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInStart;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutEncryptionRequest;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.util.SecurityHelper;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

@Async
public final class HandlerLoginStart implements Handler<MessageLoginInStart> {

    // The session id that will be used for authentication.
    static final AttributeKey<String> SESSION_ID = AttributeKey.valueOf("login-session-id");

    // The random used to generate the session ids
    private static final Random random = new Random();

    @Override
    public void handle(NetworkContext context, MessageLoginInStart message) {
        Session session = context.getSession();
        String username = message.getUsername();

        if (session.getServer().getOnlineMode()) {
            byte[] publicKey = SecurityHelper.generateX509Key(session.getServer().getKeyPair()
                    .getPublic()).getEncoded(); // Convert to X509 format
            byte[] verifyToken = SecurityHelper.generateVerifyToken();

            final String sessionId = Long.toString(random.nextLong(), 16).trim();
            context.getChannel().attr(SESSION_ID).set(sessionId);

            // Set verify data on session for use in the response handler
            session.setVerifyToken(verifyToken);
            session.setVerifyUsername(username);

            // Send created request message and wait for the response
            session.send(new MessageLoginOutEncryptionRequest(sessionId, publicKey, verifyToken));
        } else {
            ProxyData proxy = session.getProxyData();
            LanternGameProfile profile;
            if (proxy == null) {
                UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
                profile = new LanternGameProfile(uuid, username);
            } else {
                profile = new LanternGameProfile(proxy.getUniqueId(), username, proxy.getProperties());
            }
            session.messageReceived(new MessageLoginInFinish(profile));
        }
    }
}
