package org.lanternpowered.server.network.vanilla.message.codec.login;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.codec.object.VarInt;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutEncryptionRequest;

public final class CodecLoginOutEncryptionRequest implements Codec<MessageLoginOutEncryptionRequest> {

    @Override
    public ByteBuf encode(CodecContext context, MessageLoginOutEncryptionRequest message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();

        byte[] publicKey = message.getPublicKey();
        byte[] verifyToken = message.getVerifyToken();

        // Not used
        context.write(buf, String.class, "");

        // Write the public key
        context.write(buf, VarInt.class, VarInt.of(publicKey.length));
        buf.writeBytes(publicKey);

        // Write the verify token
        context.write(buf, VarInt.class, VarInt.of(verifyToken.length));
        buf.writeBytes(verifyToken);

        return buf;
    }

    @Override
    public MessageLoginOutEncryptionRequest decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new UnsupportedOperationException();
    }

}
