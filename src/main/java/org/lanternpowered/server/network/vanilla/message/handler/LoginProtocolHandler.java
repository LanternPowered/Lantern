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
package org.lanternpowered.server.network.vanilla.message.handler;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.network.NetworkSession;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInStart;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.handler.Async;
import org.lanternpowered.server.network.message.handler.ContextInject;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.pipeline.MessageCompressionHandler;
import org.lanternpowered.server.network.pipeline.MessageEncryptionHandler;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInEncryptionResponse;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInFinish;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInStart;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutEncryptionRequest;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutSetCompression;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutSuccess;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.profile.LanternProfileProperty;
import org.lanternpowered.server.util.SecurityHelper;
import org.lanternpowered.server.util.UUIDHelper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.profile.GameProfileCache;
import org.spongepowered.api.profile.property.ProfileProperty;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Handles all the {@link Message}s that are
 * received in the login protocol state.
 */
public final class LoginProtocolHandler {

    // The spoofed game profile that may be provided by proxies
    static final AttributeKey<LanternGameProfile> SPOOFED_GAME_PROFILE = AttributeKey.valueOf("spoofed-game-profile");

    // The random used to generate the session ids
    private static final Random RANDOM = new Random();

    private final static String AUTH_BASE_URL =
            "https://sessionserver.mojang.com/session/minecraft/hasJoined";
    private final static Gson GSON = new Gson();

    @ContextInject private NetworkSession session;
    @ContextInject private Channel channel;

    private String username;
    private String sessionId;
    private byte[] verifyToken;

    @Async
    @Handler
    private void handleLoginStart(MessageLoginInStart message) {
        final String username = message.getUsername();

        if (this.session.getServer().getOnlineMode()) {
            // Convert to X509 format
            final byte[] publicKey = SecurityHelper.generateX509Key(this.session.getServer().getKeyPair().getPublic()).getEncoded();
            final byte[] verifyToken = SecurityHelper.generateVerifyToken();
            final String sessionId = Long.toString(RANDOM.nextLong(), 16).trim();

            // Store the auth data
            this.username = username;
            this.sessionId = sessionId;
            this.verifyToken = verifyToken;

            // Send created request message and wait for the response
            this.session.send(new MessageLoginOutEncryptionRequest(sessionId, publicKey, verifyToken));
        } else {
            // Remove the encryption handler placeholder
            this.channel.pipeline().remove(NetworkSession.ENCRYPTION);
            LanternGameProfile profile = this.channel.attr(SPOOFED_GAME_PROFILE).getAndSet(null);
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
                    final UUID uniqueId = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
                    profile = new LanternGameProfile(uniqueId, username);
                }
            }
            session.messageReceived(new MessageLoginInFinish(profile));
        }
    }

    @Async
    @Handler
    private void handleLoginFinish(MessageLoginInFinish message) {
        final LanternGameProfile gameProfile = message.getGameProfile();
        int compressionThreshold = Lantern.getGame().getGlobalConfig().getNetworkCompressionThreshold();
        if (compressionThreshold != -1) {
            this.session.sendWithFuture(new MessageLoginOutSetCompression(compressionThreshold)).addListener(future ->
                    this.channel.pipeline().replace(NetworkSession.COMPRESSION, NetworkSession.COMPRESSION,
                            new MessageCompressionHandler(compressionThreshold)));
        } else {
            // Remove the compression handler placeholder
            this.channel.pipeline().remove(NetworkSession.COMPRESSION);
        }
        final GameProfileCache gameProfileCache = Lantern.getGame().getGameProfileManager().getCache();
        // Store the old profile temporarily
        gameProfileCache.getById(gameProfile.getUniqueId()).ifPresent(
                profile -> this.channel.attr(NetworkSession.PREVIOUS_GAME_PROFILE).set(profile));
        // Cache the new profile
        gameProfileCache.add(gameProfile, true, null);
        this.session.sendWithFuture(new MessageLoginOutSuccess(gameProfile.getUniqueId(), gameProfile.getName().get()))
                .addListener(future -> {
                    this.session.setGameProfile(gameProfile);
                    this.session.setProtocolState(ProtocolState.FORGE_HANDSHAKE);
                    this.session.messageReceived(new MessageForgeHandshakeInStart());
                });
    }

    @Async
    @Handler
    private void handleEncryptionResponse(MessageLoginInEncryptionResponse message) {
        final PrivateKey privateKey = this.session.getServer().getKeyPair().getPrivate();

        // Create rsaCipher
        Cipher rsaCipher;
        try {
            rsaCipher = Cipher.getInstance("RSA");
        } catch (GeneralSecurityException e) {
            Lantern.getLogger().error("Could not initialize RSA cipher", e);
            this.session.disconnect(t("Unable to initialize RSA cipher."));
            return;
        }

        // Decrypt shared secret
        SecretKey sharedSecret;
        try {
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            sharedSecret = new SecretKeySpec(rsaCipher.doFinal(message.getSharedSecret()), "AES");
        } catch (Exception e) {
            Lantern.getLogger().warn("Could not decrypt shared secret", e);
            this.session.disconnect(t("Unable to decrypt shared secret."));
            return;
        }

        // Decrypt verify token
        byte[] verifyToken;
        try {
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            verifyToken = rsaCipher.doFinal(message.getVerifyToken());
        } catch (Exception e) {
            Lantern.getLogger().warn("Could not decrypt verify token", e);
            this.session.disconnect(t("Unable to decrypt verify token."));
            return;
        }

        // Check verify token
        if (!Arrays.equals(verifyToken, this.verifyToken)) {
            this.session.disconnect(t("Invalid verify token."));
            return;
        }

        // Initialize stream encryption
        this.session.getChannel().pipeline().replace(NetworkSession.ENCRYPTION, NetworkSession.ENCRYPTION,
                new MessageEncryptionHandler(sharedSecret));

        // Create hash for auth
        String hash;
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-1");

            digest.update(this.sessionId.getBytes());
            digest.update(sharedSecret.getEncoded());
            digest.update(this.session.getServer().getKeyPair().getPublic().getEncoded());

            // BigInteger takes care of sign and leading zeroes
            hash = new BigInteger(digest.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            Lantern.getLogger().error("Unable to generate SHA-1 digest", e);
            this.session.disconnect(t("Failed to hash login data."));
            return;
        }
        String preventProxiesIp = null;
        if (Lantern.getGame().getGlobalConfig().shouldPreventProxyConnections()) {
            final InetAddress address = this.session.getAddress().getAddress();
            if (!isLocalAddress(address)) { // Ignore local addresses, they will always fail
                try {
                    preventProxiesIp = URLEncoder.encode(address.getHostAddress(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    Lantern.getLogger().error("Failed to encode the ip address to prevent proxies.", e);
                    this.session.disconnect(t("Something funky happened."));
                    return;
                }
            }
        }
        final String preventProxiesIp1 = preventProxiesIp;
        Lantern.getScheduler().submitAsyncTask(() -> performAuth(hash, preventProxiesIp1));
    }

    // https://stackoverflow.com/questions/2406341/how-to-check-if-an-ip-address-is-the-local-host-on-a-multi-homed-system
    private static boolean isLocalAddress(InetAddress address) {
        // Check if the address is a valid special local or loop back
        if (address.isAnyLocalAddress() || address.isLoopbackAddress()) {
            return true;
        }
        // Check if the address is defined on any interface
        try {
            return NetworkInterface.getByInetAddress(address) != null;
        } catch (SocketException e) {
            return false;
        }
    }

    private void performAuth(String hash, @Nullable String preventProxiesIp) {
        final String postUrl = AUTH_BASE_URL + "?username=" + this.username + "&serverId=" + hash +
                (preventProxiesIp == null ? "" : "?ip=" + preventProxiesIp);
        try {
            // Authenticate
            URLConnection connection = new URL(postUrl).openConnection();

            JsonObject json;
            try (InputStream is = connection.getInputStream()) {
                if (is.available() == 0) {
                    this.session.disconnect(t("Invalid username or session id!"));
                    return;
                }
                try {
                    json = GSON.fromJson(new InputStreamReader(is), JsonObject.class);
                } catch (Exception e) {
                    Lantern.getLogger().warn("Username \"{}\" failed to authenticate!", username);
                    this.session.disconnect(t("multiplayer.disconnect.unverified_username"));
                    return;
                }
            }

            final String name = json.get("name").getAsString();
            final String id = json.get("id").getAsString();

            // Parse UUID
            final UUID uuid;

            try {
                uuid = UUIDHelper.fromFlatString(id);
            } catch (IllegalArgumentException e) {
                Lantern.getLogger().error("Returned authentication UUID invalid: {}", id, e);
                this.session.disconnect(t("Invalid UUID."));
                return;
            }

            final Multimap<String, ProfileProperty> properties = LanternProfileProperty
                    .createPropertiesMapFromJson(json.getAsJsonArray("properties"));
            final LanternGameProfile gameProfile = new LanternGameProfile(uuid, name, properties);

            Lantern.getLogger().info("Finished authenticating.");

            final Cause cause = Cause.of(EventContext.empty(), this.session, gameProfile);
            final ClientConnectionEvent.Auth event = SpongeEventFactory.createClientConnectionEventAuth(cause,
                    this.session, new MessageEvent.MessageFormatter(t("multiplayer.disconnect.not_allowed_to_join")), gameProfile, false);

            Sponge.getEventManager().post(event);
            if (event.isCancelled()) {
                this.session.disconnect(event.isMessageCancelled() ? t("multiplayer.disconnect.generic") : event.getMessage());
            } else {
                this.session.messageReceived(new MessageLoginInFinish(gameProfile));
            }
        } catch (Exception e) {
            Lantern.getLogger().error("Error in authentication thread", e);
            this.session.disconnect(t("Internal error during authentication."));
        }
    }
}
