/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.network.vanilla.message.type.login;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;

public final class MessageLoginOutEncryptionRequest implements Message {

    private final String sessionId;
    private final byte[] publicKey;
    private final byte[] verifyToken;

    /**
     * Creates a new encryption request message.
     * 
     * @param publicKey The public key
     * @param verifyToken The verify token
     */
    public MessageLoginOutEncryptionRequest(String sessionId, byte[] publicKey, byte[] verifyToken) {
        this.verifyToken = checkNotNull(verifyToken, "verifyToken");
        this.publicKey = checkNotNull(publicKey, "publicKey");
        this.sessionId = checkNotNull(sessionId, "sessionId");
    }

    /**
     * Gets the public key of the encryption request.
     * 
     * @return The public key
     */
    public byte[] getPublicKey() {
        return this.publicKey;
    }

    /**
     * Gets the verify token of the encryption request.
     * 
     * @return The verify token
     */
    public byte[] getVerifyToken() {
        return this.verifyToken;
    }

    /**
     * Gets the session id (hash) that should be
     * used for authenticating.
     *
     * @return The session id
     */
    public String getSessionId() {
        return this.sessionId;
    }

}
