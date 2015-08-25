package org.lanternpowered.server.network.vanilla.message.type.login;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;

public final class MessageLoginOutEncryptionRequest implements Message {

    private final byte[] publicKey;
    private final byte[] verifyToken;

    /**
     * Creates a new encryption request message.
     * 
     * @param publicKey the public key
     * @param verifyToken the verify token
     */
    public MessageLoginOutEncryptionRequest(byte[] publicKey, byte[] verifyToken) {
        this.verifyToken = checkNotNull(verifyToken, "verify token");
        this.publicKey = checkNotNull(publicKey, "public key");
    }

    /**
     * Gets the public key of the encryption request.
     * 
     * @return the public key
     */
    public byte[] getPublicKey() {
        return this.publicKey;
    }

    /**
     * Gets the verify token of the encryption request.
     * 
     * @return the verify token
     */
    public byte[] getVerifyToken() {
        return this.verifyToken;
    }

}
