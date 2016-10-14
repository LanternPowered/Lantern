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
/*
 * Copyright (c) 2011-2014 Glowstone - Tad Hardesty
 * Copyright (c) 2010-2011 Lightstone - Graham Edgecombe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.vanilla.message.handler.login;

import io.netty.util.AttributeKey;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.Async;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.NetworkSession;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInFinish;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInStart;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutEncryptionRequest;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.util.SecurityHelper;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Async
public final class HandlerLoginStart implements Handler<MessageLoginInStart> {

    // The data that will be used for authentication.
    static final AttributeKey<LoginAuthData> AUTH_DATA = AttributeKey.valueOf("login-auth-data");

    // The spoofed game profile that may be provided by proxies
    public static final AttributeKey<LanternGameProfile> SPOOFED_GAME_PROFILE = AttributeKey.valueOf("spoofed-game-profile");

    // The random used to generate the session ids
    private static final Random RANDOM = new Random();

    @Override
    public void handle(NetworkContext context, MessageLoginInStart message) {
        final NetworkSession session = context.getSession();
        final String username = message.getUsername();

        if (session.getServer().getOnlineMode()) {
            // Convert to X509 format
            final byte[] publicKey = SecurityHelper.generateX509Key(session.getServer().getKeyPair().getPublic()).getEncoded();
            final byte[] verifyToken = SecurityHelper.generateVerifyToken();
            final String sessionId = Long.toString(RANDOM.nextLong(), 16).trim();

            // Store the auth data
            context.getChannel().attr(AUTH_DATA).set(new LoginAuthData(username, sessionId, verifyToken));
            // Send created request message and wait for the response
            session.send(new MessageLoginOutEncryptionRequest(sessionId, publicKey, verifyToken));
        } else {
            // Remove the encryption handler placeholder
            context.getChannel().pipeline().remove(NetworkSession.ENCRYPTION);
            LanternGameProfile profile = context.getChannel().attr(SPOOFED_GAME_PROFILE).getAndRemove();
            if (profile != null) {
                profile = new LanternGameProfile(profile.getUniqueId(), username, profile.getPropertyMap());
            } else {
                // Try the online id first
                try {
                    profile = (LanternGameProfile) Lantern.getGame().getGameProfileManager().get(username).get();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    // Generate a offline id
                    UUID uniqueId = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
                    profile = new LanternGameProfile(uniqueId, username);
                }
            }
            session.messageReceived(new MessageLoginInFinish(profile));
        }
    }
}
