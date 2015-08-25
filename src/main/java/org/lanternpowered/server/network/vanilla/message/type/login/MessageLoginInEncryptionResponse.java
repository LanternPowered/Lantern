package org.lanternpowered.server.network.vanilla.message.type.login;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;

public final class MessageLoginInEncryptionResponse implements Message {

    private final byte[] sharedSecret;
    private final byte[] verifyToken;

    /**
     * Creates a new encryption response message.
     * 
     * @param sharedSecret the shared secret
     * @param verifyToken the verify token
     */
    public MessageLoginInEncryptionResponse(byte[] sharedSecret, byte[] verifyToken) {
        this.sharedSecret = checkNotNull(sharedSecret, "sharedSecret");
        this.verifyToken = checkNotNull(verifyToken, "verifyToken");
    }

    /**
     * Gets the shared secret of the encryption response.
     * 
     * @return the shared secret
     */
    public byte[] getSharedSecret() {
        return this.sharedSecret;
    }

    /**
     * Gets the verify token of the encryption response.
     * 
     * @return the verify token
     */
    public byte[] getVerifyToken() {
        return this.verifyToken;
    }

}
