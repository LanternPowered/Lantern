package org.lanternpowered.server.network.vanilla.message.codec.handshake;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.codec.object.VarInt;
import org.lanternpowered.server.network.vanilla.message.type.handshake.MessageHandshakeIn;

public final class CodecHandshakeIn implements Codec<MessageHandshakeIn> {

    @Override
    public ByteBuf encode(CodecContext context, MessageHandshakeIn message) throws CodecException {
        throw new CodecException();
    }

    @Override
    public MessageHandshakeIn decode(CodecContext context, ByteBuf buf) throws CodecException {
        int protocol = context.read(buf, VarInt.class).value();
        String address = context.read(buf, String.class);
        short port = buf.readShort();
        int state = context.read(buf, VarInt.class).value();
        return new MessageHandshakeIn(state, address, port, protocol);
    }

}
