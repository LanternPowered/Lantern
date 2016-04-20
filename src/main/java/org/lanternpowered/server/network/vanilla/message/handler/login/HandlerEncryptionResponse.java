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

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInEncryptionResponse;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInFinish;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.profile.LanternProfileProperty;
import org.lanternpowered.server.scheduler.LanternScheduler;
import org.lanternpowered.server.util.UUIDHelper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.text.Text;

import java.io.IOException;
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

    private final String authBaseUrl = "https://sessionserver.mojang.com/session/minecraft/hasJoined";
    private final Gson gson = new Gson();

    @Override
    public void handle(NetworkContext context, MessageLoginInEncryptionResponse message) {
        Session session = context.getSession();
        PrivateKey privateKey = session.getServer().getKeyPair().getPrivate();

        // Create rsaCipher
        Cipher rsaCipher;
        try {
            rsaCipher = Cipher.getInstance("RSA");
        } catch (GeneralSecurityException e) {
            Lantern.getLogger().error("Could not initialize RSA cipher", e);
            session.disconnect("Unable to initialize RSA cipher.");
            return;
        }

        // Decrypt shared secret
        SecretKey sharedSecret;
        try {
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            sharedSecret = new SecretKeySpec(rsaCipher.doFinal(message.getSharedSecret()), "AES");
        } catch (Exception e) {
            Lantern.getLogger().warn("Could not decrypt shared secret", e);
            session.disconnect("Unable to decrypt shared secret.");
            return;
        }

        // Decrypt verify token
        byte[] verifyToken;
        try {
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            verifyToken = rsaCipher.doFinal(message.getVerifyToken());
        } catch (Exception e) {
            Lantern.getLogger().warn("Could not decrypt verify token", e);
            session.disconnect("Unable to decrypt verify token.");
            return;
        }

        LoginAuthData authData = context.getChannel().attr(HandlerLoginStart.AUTH_DATA).getAndRemove();

        // Check verify token
        if (!Arrays.equals(verifyToken, authData.getVerifyToken())) {
            session.disconnect("Invalid verify token.");
            return;
        }

        // Initialize stream encryption
        session.setEncryption(sharedSecret);

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
            session.disconnect("Failed to hash login data.");
            return;
        }

        LanternScheduler.getInstance().submitAsyncTask(() -> {
            performAuth(session, authData.getUsername(), hash);
            return null;
        });
    }

    private void performAuth(Session session, String username, String hash) {
        final String postUrl = this.authBaseUrl + "?username=" + username + "&serverId=" + hash;
        try {
            // Authenticate
            URLConnection connection = new URL(postUrl).openConnection();

            JsonObject json;
            try (InputStream is = connection.getInputStream()) {
                if (is.available() == 0) {
                    session.disconnect("Invalid username or session id!");
                    return;
                }
                try {
                    json = this.gson.fromJson(new InputStreamReader(is), JsonObject.class);
                } catch (Exception e) {
                    Lantern.getLogger().warn("Username \"{}\" failed to authenticate!", username);
                    session.disconnect("Failed to verify username!");
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
                session.disconnect("Invalid UUID.");
                return;
            }

            JsonArray propsArray = json.getAsJsonArray("properties");

            // Parse properties
            Multimap<String, ProfileProperty> properties = LinkedHashMultimap.create();
            for (JsonElement element : propsArray) {
                JsonObject json0 = element.getAsJsonObject();
                String propName = json0.get("name").getAsString();
                String value = json0.get("value").getAsString();
                String signature = json0.has("signature") ? json0.get("signature").getAsString() : null;
                properties.put(propName, new LanternProfileProperty(propName, value, signature));
            }

            LanternGameProfile gameProfile = new LanternGameProfile(uuid, name, properties);

            Lantern.getLogger().info("Finished authenticating.");
            Lantern.getGame().getGameProfileManager().getCache().add(gameProfile, true, null);

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
            session.disconnect("Internal error during authentication.");
        }
    }
}
