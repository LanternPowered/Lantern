package org.lanternpowered.server.network.vanilla.message.codec.login;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.codec.object.VarInt;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInEncryptionResponse;

public final class CodecLoginInEncryptionResponse implements Codec<MessageLoginInEncryptionResponse> {

    @Override
    public ByteBuf encode(CodecContext context, MessageLoginInEncryptionResponse message) throws CodecException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MessageLoginInEncryptionResponse decode(CodecContext context, ByteBuf buf) throws CodecException {
        byte[] sharedSecret = new byte[context.read(buf, VarInt.class).value()];
        buf.readBytes(sharedSecret);
        byte[] verifyToken = new byte[context.read(buf, VarInt.class).value()];
        buf.readBytes(verifyToken);
        return new MessageLoginInEncryptionResponse(sharedSecret, verifyToken);
    }

}
