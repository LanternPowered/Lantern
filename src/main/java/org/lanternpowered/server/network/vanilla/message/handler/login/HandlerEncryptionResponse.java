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

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.pipeline.MessageEncryptionHandler;
import org.lanternpowered.server.network.NetworkSession;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInEncryptionResponse;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInFinish;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.profile.LanternProfileProperty;
import org.lanternpowered.server.util.UUIDHelper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.profile.property.ProfileProperty;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public final class HandlerEncryptionResponse implements Handler<MessageLoginInEncryptionResponse> {

    private final static String AUTH_BASE_URL =
            "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=%s&serverId=%s";
    private final static Gson GSON = new Gson();

    @Override
    public void handle(NetworkContext context, MessageLoginInEncryptionResponse message) {
        final NetworkSession session = context.getSession();
        final PrivateKey privateKey = session.getServer().getKeyPair().getPrivate();

        // Create rsaCipher
        Cipher rsaCipher;
        try {
            rsaCipher = Cipher.getInstance("RSA");
        } catch (GeneralSecurityException e) {
            Lantern.getLogger().error("Could not initialize RSA cipher", e);
            session.disconnect(t("Unable to initialize RSA cipher."));
            return;
        }

        // Decrypt shared secret
        SecretKey sharedSecret;
        try {
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            sharedSecret = new SecretKeySpec(rsaCipher.doFinal(message.getSharedSecret()), "AES");
        } catch (Exception e) {
            Lantern.getLogger().warn("Could not decrypt shared secret", e);
            session.disconnect(t("Unable to decrypt shared secret."));
            return;
        }

        // Decrypt verify token
        byte[] verifyToken;
        try {
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            verifyToken = rsaCipher.doFinal(message.getVerifyToken());
        } catch (Exception e) {
            Lantern.getLogger().warn("Could not decrypt verify token", e);
            session.disconnect(t("Unable to decrypt verify token."));
            return;
        }

        LoginAuthData authData = context.getChannel().attr(HandlerLoginStart.AUTH_DATA).getAndRemove();

        // Check verify token
        if (!Arrays.equals(verifyToken, authData.getVerifyToken())) {
            session.disconnect(t("Invalid verify token."));
            return;
        }

        // Initialize stream encryption
        session.getChannel().pipeline().replace(NetworkSession.ENCRYPTION, NetworkSession.ENCRYPTION,
                new MessageEncryptionHandler(sharedSecret));

        // Create hash for auth
        String hash;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");

            digest.update(authData.getSessionId().getBytes());
            digest.update(sharedSecret.getEncoded());
            digest.update(session.getServer().getKeyPair().getPublic().getEncoded());

            // BigInteger takes care of sign and leading zeroes
            hash = new BigInteger(digest.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            Lantern.getLogger().error("Unable to generate SHA-1 digest", e);
            session.disconnect(t("Failed to hash login data."));
            return;
        }

        Lantern.getScheduler().submitAsyncTask(() -> performAuth(session, authData.getUsername(), hash));
    }

    private void performAuth(NetworkSession session, String username, String hash) {
        final String postUrl = String.format(AUTH_BASE_URL, username, hash);
        try {
            // Authenticate
            URLConnection connection = new URL(postUrl).openConnection();

            JsonObject json;
            try (InputStream is = connection.getInputStream()) {
                if (is.available() == 0) {
                    session.disconnect(t("Invalid username or session id!"));
                    return;
                }
                try {
                    json = GSON.fromJson(new InputStreamReader(is), JsonObject.class);
                } catch (Exception e) {
                    Lantern.getLogger().warn("Username \"{}\" failed to authenticate!", username);
                    session.disconnect(t("Failed to verify username!"));
                    return;
                }
            }

            String name = json.get("name").getAsString();
            String id = json.get("id").getAsString();

            // Parse UUID
            UUID uuid;

            try {
                uuid = UUIDHelper.fromFlatString(id);
            } catch (IllegalArgumentException e) {
                Lantern.getLogger().error("Returned authentication UUID invalid: {}", id, e);
                session.disconnect(t("Invalid UUID."));
                return;
            }

            Multimap<String, ProfileProperty> properties = LanternProfileProperty.createPropertiesMapFromJson(json.getAsJsonArray("properties"));
            LanternGameProfile gameProfile = new LanternGameProfile(uuid, name, properties);

            Lantern.getLogger().info("Finished authenticating.");

            ClientConnectionEvent.Auth event = SpongeEventFactory.createClientConnectionEventAuth(Cause.source(gameProfile).build(), session,
                    new MessageEvent.MessageFormatter(t("disconnect.notAllowedToJoin")), gameProfile, false);

            Sponge.getEventManager().post(event);
            if (event.isCancelled()) {
                session.disconnect(event.isMessageCancelled() ? t("disconnect.disconnected") : event.getMessage());
            } else {
                session.messageReceived(new MessageLoginInFinish(gameProfile));
            }
        } catch (Exception e) {
            Lantern.getLogger().error("Error in authentication thread", e);
            session.disconnect(t("Internal error during authentication."));
        }
    }
}
