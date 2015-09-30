package org.lanternpowered.server.network.vanilla.message.handler.login;

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
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.game.LanternGameProfile;
import org.lanternpowered.server.game.LanternGameProfile.Property;
import org.lanternpowered.server.network.forge.message.handshake.HandshakePhase;
import org.lanternpowered.server.network.forge.message.handshake.MessageHandshakeInOutHello;
import org.lanternpowered.server.network.forge.message.handshake.MessageHandshakeInStart;
import org.lanternpowered.server.network.forge.message.handshake.ServerHandshakePhase;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInEncryptionResponse;
import org.lanternpowered.server.util.UUIDHelper;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class HandlerEncryptionResponse implements Handler<MessageLoginInEncryptionResponse> {

    private final Random random = new Random();
    private final Gson gson = new Gson();

    @Override
    public void handle(Session session, MessageLoginInEncryptionResponse message) {
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

        // initialize stream encryption
        session.setExcryption(sharedSecret);

        // create hash for auth
        String hash;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            String sessionId = Long.toString(this.random.nextLong(), 16).trim();

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

        // start auth thread
        Thread clientAuthThread = new Thread(new ClientAuthRunnable(session, session.getVerifyUsername(), hash));
        clientAuthThread.setName("ClientAuthThread{" + session.getVerifyUsername() + "}");
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
                // authenticate
                URLConnection connection = new URL(this.postURL).openConnection();

                JsonObject json;
                try (InputStream is = connection.getInputStream()) {
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
                List<Property> properties = Lists.newArrayListWithCapacity(propsArray.size());
                for (JsonElement element : propsArray) {
                    JsonObject json0 = element.getAsJsonObject();
                    String propName = json0.get("name").getAsString();
                    String value = json0.get("value").getAsString();
                    String signature = json0.has("signature") ? json0.get("signature").getAsString() : null;
                    properties.add(new Property(propName, value, signature));
                }

                /*
                final AsyncPlayerPreLoginEvent event = EventFactory.onPlayerPreLogin(name, session.getAddress(), uuid);
                if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
                    session.disconnect(event.getKickMessage(), true);
                    return;
                }*/

                session.setPlayer(new LanternGameProfile(uuid, name, properties));
                session.setProtocolState(ProtocolState.FORGE_HANDSHAKE);
                session.messageReceived(new MessageHandshakeInStart());
            } catch (Exception e) {
                LanternGame.log().error("Error in authentication thread", e);
                this.session.disconnect("Internal error during authentication.", true);
            }
        }
    }
}
