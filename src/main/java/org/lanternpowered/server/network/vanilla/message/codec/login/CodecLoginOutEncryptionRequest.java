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
package org.lanternpowered.server.network.vanilla.message.codec.login;

import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutEncryptionRequest;

public final class CodecLoginOutEncryptionRequest implements Codec<MessageLoginOutEncryptionRequest> {

    @Override
    public ByteBuffer encode(CodecContext context, MessageLoginOutEncryptionRequest message) throws CodecException {
        ByteBuffer buf = context.byteBufAlloc().buffer();

        byte[] publicKey = message.getPublicKey();
        byte[] verifyToken = message.getVerifyToken();

        buf.writeString(message.getSessionId());

        // Write the public key
        buf.writeByteArray(publicKey);
        // Write the verify token
        buf.writeByteArray(verifyToken);

        return buf;
    }
}
