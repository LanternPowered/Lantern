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

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInEncryptionResponse;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInFinish;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.profile.LanternProfileProperty;
import org.lanternpowered.server.util.UUIDHelper;
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
            LanternGame.log().error("Could not initialize RSA cipher", e);
            session.disconnect("Unable to initialize RSA cipher.");
            return;
        }

        // Decrypt shared secret
        SecretKey sharedSecret;
        try {
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            sharedSecret = new SecretKeySpec(rsaCipher.doFinal(message.getSharedSecret()), "AES");
        } catch (Exception e) {
            LanternGame.log().warn("Could not decrypt shared secret", e);
            session.disconnect("Unable to decrypt shared secret.");
            return;
        }

        // Decrypt verify token
        byte[] verifyToken;
        try {
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            verifyToken = rsaCipher.doFinal(message.getVerifyToken());
        } catch (Exception e) {
            LanternGame.log().warn("Could not decrypt verify token", e);
            session.disconnect("Unable to decrypt verify token.");
            return;
        }

        // Check verify token
        if (!Arrays.equals(verifyToken, session.getVerifyToken())) {
            session.disconnect("Invalid verify token.");
            return;
        }

        // Initialize stream encryption
        session.setEncryption(sharedSecret);

        // Create hash for auth
        String hash;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            String sessionId = context.getChannel().attr(HandlerLoginStart.SESSION_ID).getAndRemove();

            digest.update(sessionId.getBytes());
            digest.update(sharedSecret.getEncoded());
            digest.update(session.getServer().getKeyPair().getPublic().getEncoded());

            // BigInteger takes care of sign and leading zeroes
            hash = new BigInteger(digest.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            LanternGame.log().error("Unable to generate SHA-1 digest", e);
            session.disconnect("Failed to hash login data.");
            return;
        }

        // Start auth thread
        Thread clientAuthThread = new Thread(new ClientAuthRunnable(session, session.getVerifyUsername(), hash));
        clientAuthThread.setName("auth{" + session.getVerifyUsername() + "}");
        clientAuthThread.start();
    }

    private class ClientAuthRunnable implements Runnable {

        private static final String BASE_URL = "https://sessionserver.mojang.com/session/minecraft/hasJoined";

        private final Session session;
        private final String username;
        private final String postURL;

        private ClientAuthRunnable(Session session, String username, String hash) {
            this.postURL = BASE_URL + "?username=" + username + "&serverId=" + hash;
            this.session = session;
            this.username = username;
        }

        @Override
        public void run() {
            try {
                // Authenticate
                URLConnection connection = new URL(this.postURL).openConnection();

                JsonObject json;
                try (InputStream is = connection.getInputStream()) {
                    if (is.available() == 0) {
                        this.session.disconnect("Invalid username or session id!");
                        return;
                    }
                    try {
                        json = gson.fromJson(new InputStreamReader(is), JsonObject.class);
                    } catch (Exception e) {
                        LanternGame.log().warn("Username \"" + this.username + "\" failed to authenticate!");
                        this.session.disconnect("Failed to verify username!");
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
                    LanternGame.log().error("Returned authentication UUID invalid: " + id, e);
                    this.session.disconnect("Invalid UUID.");
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

                LanternGame.log().info("Finished authenticating.");
                LanternGame.get().getGameProfileManager().putProfile(gameProfile, true);
                this.session.messageReceived(new MessageLoginInFinish(gameProfile));
            } catch (Exception e) {
                LanternGame.log().error("Error in authentication thread", e);
                this.session.disconnect("Internal error during authentication.", true);
            }
        }
    }
}
