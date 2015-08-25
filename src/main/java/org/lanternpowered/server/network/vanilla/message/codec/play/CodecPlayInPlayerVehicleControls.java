package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.internal.MessagePlayInPlayerVehicleControls;

public final class CodecPlayInPlayerVehicleControls implements Codec<MessagePlayInPlayerVehicleControls> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayInPlayerVehicleControls message) throws CodecException {
        throw new CodecException();
    }

    @Override
    public MessagePlayInPlayerVehicleControls decode(CodecContext context, ByteBuf buf) throws CodecException {
        float sideways = buf.readFloat();
        float forwards = buf.readFloat();
        byte flags = buf.readByte();
        boolean jump = (flags & 0x1) != 0;
        boolean sneak = (flags & 0x2) != 0;
        return new MessagePlayInPlayerVehicleControls(forwards, sideways, jump, sneak);
    }

}
