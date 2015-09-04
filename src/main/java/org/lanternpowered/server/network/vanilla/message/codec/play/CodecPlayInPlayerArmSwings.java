package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.entity.living.player.PlayerHand;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerArmSwings;

public final class CodecPlayInPlayerArmSwings implements Codec<MessagePlayInPlayerArmSwings> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayInPlayerArmSwings message) throws CodecException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MessagePlayInPlayerArmSwings decode(CodecContext context, ByteBuf buf) throws CodecException {
        return new MessagePlayInPlayerArmSwings(PlayerHand.RIGHT);
    }

}
